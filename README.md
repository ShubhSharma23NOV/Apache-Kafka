# Kafka Microservices with Spring Boot

A beginner-friendly demonstration of **event-driven communication** between two independent Spring Boot services using **Apache Kafka**. The project includes Docker-based Kafka infrastructure and a Kafka UI dashboard for inspecting topics, messages, and consumer groups.

## Architecture

```text
                   HTTP POST /api/messages
                            |
                            v
                 +---------------------+
                 |  Publisher Service  |
                 |  Spring Boot :8081  |
                 +---------------------+
                            |
                     KafkaTemplate
                            |
                            v
                 +---------------------+
                 |    Apache Kafka     |
                 | Topic: demo-topic   |
                 | Host port: 9092     |
                 +---------------------+
                            |
                      @KafkaListener
                            |
                            v
                 +---------------------+
                 | Subscriber Service  |
                 |  Spring Boot :8082  |
                 +---------------------+

         Kafka UI: http://localhost:8085
```

The publisher sends messages to a Kafka topic. The subscriber consumes them asynchronously, without a direct HTTP call between the two services.

## Tech Stack

- Java 17+ and Spring Boot
- Spring for Apache Kafka (`spring-kafka`)
- Maven
- Apache Kafka 3.9.1 (Docker)
- Kafka UI (Provectus)
- Docker Compose
- Postman or cURL for API testing

## Project Structure

```text
kafka-microservices/
├── docker-compose.yml
├── publisher-service/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../
│       │   ├── PublisherServiceApplication.java
│       │   ├── controller/
│       │   │   └── MessageController.java
│       │   └── service/
│       │       └── KafkaProducerService.java
│       └── resources/application.yml
└── subscriber-service/
    ├── pom.xml
    └── src/main/
        ├── java/.../
        │   ├── SubscriberServiceApplication.java
        │   └── service/
        │       └── KafkaConsumerService.java
        └── resources/application.yml
```

> Package names and class names may vary slightly depending on how the services were generated.

## Prerequisites

- JDK 17 or later (compatible with your Spring Boot version)
- Docker Desktop with Docker Compose
- Maven, or the Maven wrappers (`mvnw`) included with each service
- An API client such as Postman (optional)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

Replace the placeholders with your GitHub repository details.

### 2. Start Kafka and Kafka UI

From the repository root:

```bash
docker compose up -d
docker compose ps
```

The expected local endpoints are:

| Component | Address |
| --- | --- |
| Kafka broker (host access) | `localhost:9092` |
| Kafka broker (Docker network) | `kafka:29092` |
| Kafka UI | http://localhost:8085 |
| Publisher Service | http://localhost:8081 |
| Subscriber Service | http://localhost:8082 |

> The Docker network address is for containers such as Kafka UI. Spring Boot applications running directly on the host use `localhost:9092`.

### 3. Start the subscriber

In one terminal:

```bash
cd subscriber-service
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

### 4. Start the publisher

In another terminal, from the repository root:

```bash
cd publisher-service
./mvnw spring-boot:run
```

Both services should now connect to the Kafka broker.

## Kafka Configuration

**Publisher** (`publisher-service/src/main/resources/application.yml`):

```yaml
server:
  port: 8081
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

**Subscriber** (`subscriber-service/src/main/resources/application.yml`):

```yaml
server:
  port: 8082
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: demo-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

| Kafka setting | Value | Description |
| --- | --- | --- |
| Topic | `demo-topic` | Destination for published messages |
| Consumer group | `demo-consumer-group` | Group used by the subscriber |
| Host bootstrap server | `localhost:9092` | Kafka address used by local Spring Boot services |
| Docker bootstrap server | `kafka:29092` | Kafka address used by Kafka UI |

The topic may be created automatically by the application's `NewTopic` bean. If not, create `demo-topic` in Kafka UI with one partition and replication factor one.

## Test the Message Flow

### Publish a message

Send an HTTP request to the publisher:

```http
POST http://localhost:8081/api/messages
Content-Type: text/plain

Hello Kafka from Spring Boot!
```

Or use cURL:

```bash
curl -X POST http://localhost:8081/api/messages \
  -H "Content-Type: text/plain" \
  -d "Hello Kafka from Spring Boot!"
```

The example publisher returns a response indicating that message publishing was initiated. This response alone does **not** guarantee broker acknowledgment.

### Check the subscriber

Expected console output:

```text
Subscriber received: Hello Kafka from Spring Boot!
```

### Inspect the message in Kafka UI

1. Open http://localhost:8085.
2. Select the `local-kafka` cluster.
3. Navigate to **Topics → demo-topic → Messages**.
4. Find the published message.
5. Navigate to **Consumers → demo-consumer-group** to inspect group membership and lag.

## Verify Asynchronous Communication

1. Stop the subscriber.
2. Publish a few messages through the publisher API.
3. Confirm that they appear in Kafka UI.
4. Restart the subscriber.
5. Check whether the subscriber processes the pending messages.

Kafka retains messages according to topic retention settings. A subscriber using the same consumer group can resume from committed offsets, provided the messages are still retained.

## Troubleshooting

| Problem | What to check |
| --- | --- |
| Spring Boot cannot connect to Kafka | Confirm `docker compose ps` and `localhost:9092` |
| Kafka UI shows no cluster | Check `docker logs kafka-ui` and the `kafka:29092` advertised listener |
| Topic is missing | Create `demo-topic` using Kafka UI or the `NewTopic` bean |
| Subscriber receives nothing | Check the topic name, group ID, service logs, and consumer offsets |
| IntelliJ cannot resolve `@KafkaListener` | Verify the `spring-kafka` dependency and reload Maven |
| Port already in use | Stop the conflicting process or change the configured port |

To inspect container logs:

```bash
docker compose logs -f kafka kafka-ui
```

## Stop the Environment

Stop both Spring Boot applications, then run:

```bash
docker compose down
```

## Key Concepts Demonstrated

- Event-driven communication between independent services
- Kafka producers and consumers
- Topics and partitions
- Consumer groups and offsets
- Message serialization and deserialization
- Dockerized messaging infrastructure
- Topic and consumer monitoring through Kafka UI

## Possible Enhancements

- Publish and consume JSON objects
- Add multiple consumers and partitions
- Implement retry handling and dead-letter topics
- Configure manual acknowledgments
- Add integration tests with Testcontainers
- Add structured logging and metrics

## Notes

This is a **local learning project**. Its Kafka listeners use plaintext connections, the single-broker topic has replication factor one, and Kafka UI has no authentication. Additional security, fault tolerance, and monitoring are needed before production deployment.
