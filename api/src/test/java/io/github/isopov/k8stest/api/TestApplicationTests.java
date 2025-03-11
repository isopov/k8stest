package io.github.isopov.k8stest.api;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestContainersConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.hikari.maximumPoolSize = 1"})
class TestApplicationTests {

    @Autowired
    private HikariDataSource ds;

    @Test
    void contextLoads() {
    }

    @Test
    void datasourceSize() {
        assertEquals(1, ds.getMaximumPoolSize());
    }


}
