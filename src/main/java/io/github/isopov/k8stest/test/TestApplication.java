package io.github.isopov.k8stest.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

@RestController
@RequestMapping("/messages")
class MessagesController {
    private final JdbcTemplate template;

    MessagesController(JdbcTemplate template) {
        this.template = template;
    }

    @PostMapping
    public Integer post(@RequestBody String message) {
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

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable int id) {
        return getMessage(() -> template.queryForObject("select value from message where id=?", String.class, id));
    }

//    @GetMapping("/random")
//    public ResponseEntity<String> get() {
//        return getMessage(() -> template.queryForObject("select value from message tablesample BERNOULLI (1) limit 1", String.class));
//    }

    private static ResponseEntity<String> getMessage(Supplier<String> sup) {
        try {
            var message = sup.get();
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (EmptyResultDataAccessException empty) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}

@RestController
@RequestMapping("/load")
class LoadController {
    @GetMapping("/factorial/{num}")
    public Double burn(@PathVariable int num) {
        var result = BigInteger.ONE;
        for (int i = 1; i <= num; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result.doubleValue();
    }

    @GetMapping("/allocate/{mbs}")
    public int allocate(@PathVariable int mbs) {
        final var random = ThreadLocalRandom.current();
        var dummyresult = 0;
        for (int i = 0; i < mbs; i++) {
            final byte[] mb = new byte[1024 * 1024];
            random.nextBytes(mb);
            dummyresult += sum(mb);
        }
        return dummyresult;
    }

    private static int sum(byte[] bytes) {
        var result = 0;
        for (byte b : bytes) {
            result += b;
        }
        return result;
    }
}
