package io.github.isopov.k8stest.core;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    NewTopic messagesTopic() {
        return new NewTopic("messages", 24, (short) 3);
    }
}
