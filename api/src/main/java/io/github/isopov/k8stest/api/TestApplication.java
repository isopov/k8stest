package io.github.isopov.k8stest.api;

import io.github.isopov.k8stest.core.MessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication(scanBasePackages = "io.github.isopov.k8stest")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

@RestController
@RequestMapping("/messages")
class MessagesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesController.class);

    private final MessagesService service;
    private final KafkaTemplate<String, String> kafkaTemplate;


    MessagesController(
            MessagesService service,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Integer post(@RequestBody String message) {
        var id = service.save(message);
        kafkaTemplate.send("messages", String.valueOf(id)).
                thenAccept((result) -> {
                    LOGGER.info("Sent message {} as saved to kafka", id);
                });
        return id;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable int id) throws InterruptedException {
        try {
            return new ResponseEntity<>(service.get(id), HttpStatus.OK);
        } catch (EmptyResultDataAccessException empty) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (MessagesService.RateLimitReachedException e) {
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
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

@RestController
@RequestMapping("/thread")
class ThreadController {

    @GetMapping("/name")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}