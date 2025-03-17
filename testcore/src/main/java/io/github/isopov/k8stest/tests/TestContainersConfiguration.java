package io.github.isopov.k8stest.tests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.4"));
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7.4.2")).withExposedPorts(6379);
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        //latest == 3.9.0 at the time of writing this does not work for some reason - nor kafka-native, nor kafka
        return new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1"));
    }

}