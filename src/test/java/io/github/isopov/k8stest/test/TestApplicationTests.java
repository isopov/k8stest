package io.github.isopov.k8stest.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersConfiguration.class)
@SpringBootTest
class TestApplicationTests {

	@Test
	void contextLoads() {
	}

}
