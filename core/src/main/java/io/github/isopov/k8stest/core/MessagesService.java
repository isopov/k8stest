package io.github.isopov.k8stest.core;

import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequestMapping("/messages")
public class MessagesService {
    private final JdbcTemplate template;
    private BlockingBucket rateLimiter;

    private static final int MESSAGES_RATE_LIMIT = 100;

    MessagesService(
            JdbcTemplate template,
            RedisConnectionFactory redisFactory
    ) {
        this.template = template;

        var configuration = BucketConfiguration.builder().addLimit(
                        BandwidthBuilder.builder()
                                .capacity(MESSAGES_RATE_LIMIT)
                                .refillGreedy(MESSAGES_RATE_LIMIT, Duration.ofSeconds(1))
                                .build()
                )
                .build();
        var commands = (RedisAsyncCommands<byte[], byte[]>) redisFactory.getConnection().getNativeConnection();
        var manager = Bucket4jLettuce.casBasedBuilder(commands).build();
        this.rateLimiter = manager.getProxy("messages".getBytes(), () -> configuration).asBlocking();

    }

    public Integer save(String message) {
        var keyHolder = new GeneratedKeyHolder();
        template.update(
                (Connection con) -> {
                    var ps = con.prepareStatement("insert into message(value) values(?) returning id",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, message);
                    return ps;
                }, keyHolder
        );
        return keyHolder.getKey().intValue();
    }

    public String get(int id) throws InterruptedException {
        return getMessage(() -> template.queryForObject("select value from message where id=?", String.class, id));
    }

//    @GetMapping("/random")
//    public String get() {
//        return getMessage(() -> template.queryForObject("select value from message tablesample BERNOULLI (1) limit 1", String.class));
//    }

    private String getMessage(Supplier<String> sup) throws InterruptedException {
        if (rateLimiter.tryConsume(1, Duration.ofSeconds(60))) {
            return sup.get();
        } else {
            throw new RateLimitReachedException();
        }
    }

    public static class RateLimitReachedException extends RuntimeException {
        public RateLimitReachedException() {
            super("", null, true, false);
        }
    }


}