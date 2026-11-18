package kafka.subscriber_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = "demo-topic",
            groupId = "demo-consumer-group"
    )
    public void consumeMessage(String message) {

        System.out.println(
                "Subscriber received: " + message
        );
    }
}