package kafka.publisher_service;

import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.boot.SprigApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PublisherServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublisherServiceApplication.class, args);
	}

	@Bean
	public NewTopic demoTopic() {
		return new NewTopic("demo-topic", 1, (short) 1);
	}

}
