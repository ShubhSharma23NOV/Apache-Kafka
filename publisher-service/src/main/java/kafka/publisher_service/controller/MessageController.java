package kafka.publisher_service.controller;

import kafka.publisher_service.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducerService producerService;

    public MessageController(
            KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public String sendMessage(@RequestBody String message) {
        producerService.publishMessage(message);
        return "Message publishing initiated: " + message;
    }
}