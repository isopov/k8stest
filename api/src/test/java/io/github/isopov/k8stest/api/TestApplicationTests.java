package io.github.isopov.k8stest.api;

import com.zaxxer.hikari.HikariDataSource;
import io.github.isopov.k8stest.grpccore.FactorialReply;
import io.github.isopov.k8stest.grpccore.FactorialRequest;
import io.github.isopov.k8stest.grpccore.FactorialsGrpc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import io.github.isopov.k8stest.tests.TestContainersConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    FactorialsGrpc.FactorialsBlockingStub factorialStub;

    @Test
    void factorial() throws Exception {
        Mockito.when(factorialStub.compute(Mockito.any(FactorialRequest.class))).
                thenReturn(FactorialReply.newBuilder().setValue(0).build());

        mockMvc.perform(get("/load/factorial/42"))
                .andExpect(status().isOk());
    }

    @Test
    void allocate() throws Exception {
        mockMvc.perform(get("/load/allocate/1"))
                .andExpect(status().isOk());
    }


}
