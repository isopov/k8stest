package io.github.isopov.k8stest.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@RestController
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

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
