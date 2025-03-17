package io.github.isopov.k8stest.listener;

import io.github.isopov.k8stest.core.MessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "io.github.isopov.k8stest")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


}


@Component
class MessagesListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesListener.class);

    private final MessagesService service;

    MessagesListener(MessagesService service) {
        this.service = service;
    }

    @KafkaListener(topics = "messages")
    public void processMessage(String messageStringId) throws InterruptedException {
        LOGGER.info("Processing message {}", messageStringId);
        var message = service.get(Integer.parseInt(messageStringId));
        LOGGER.info("Message is {}", message);
    }

}
