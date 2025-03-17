package io.github.isopov.k8stest.api;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import io.github.isopov.k8stest.tests.TestContainersConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestContainersConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.hikari.maximumPoolSize = 1"})
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class TestApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HikariDataSource ds;

    @Test
    void contextLoads() {
    }

    @Test
    void datasourceSize() {
        assertEquals(1, ds.getMaximumPoolSize());
    }

    @Test
    void messages() throws Exception {
        mockMvc.perform(post("/messages", "Hello").content("Hello"))
                .andExpect(status().isOk());
    }
}
