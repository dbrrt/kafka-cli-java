# Kafka CLI

A simple command-line interface for interacting with Apache Kafka.

## Docker Setup

The project includes a Docker Compose configuration for setting up a local Kafka environment. To start the Kafka cluster:

```bash
docker-compose up -d
```

This will start:
- Zookeeper (port 2181)
- Kafka broker (port 9092)
- Kafka UI (port 8080)

You can access the Kafka UI at http://localhost:8080 to monitor topics, messages, and broker status.

To stop the Kafka cluster:
```bash
docker-compose down
```

## Building

To build the project, run:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory named `kafka-cli-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Using the Makefile

For convenience, a Makefile is provided to simplify common operations:

```bash
# Build the project
make build

# Clean the project
make clean

# Show help
make help

# Produce a message
make produce MESSAGE="Hello, Kafka!" TOPIC=my-topic

# Consume messages
make consume TOPIC=my-topic GROUP_ID=my-consumer-group

# Run with custom arguments
make run ARGS="--bootstrap-servers kafka:9092 --topic test --consume"

# Show version
make version

# Show examples
make examples
```

You can customize the default values by overriding the variables:

```bash
make consume BOOTSTRAP_SERVERS=kafka:9092 TOPIC=my-topic GROUP_ID=my-group
```

## Direct Usage

The CLI supports both producing and consuming messages from Kafka topics. Here are some examples:

### Producing Messages

```bash
java -jar target/kafka-cli-1.0-SNAPSHOT-jar-with-dependencies.jar \
    --bootstrap-servers localhost:9092 \
    --topic my-topic \
    --produce \
    --message "Hello, Kafka!"
```

### Consuming Messages

```bash
java -jar target/kafka-cli-1.0-SNAPSHOT-jar-with-dependencies.jar \
    --bootstrap-servers localhost:9092 \
    --topic my-topic \
    --consume \
    --group-id my-consumer-group
```

## Options

- `-b, --bootstrap-servers`: Kafka bootstrap servers (required)
- `-t, --topic`: Kafka topic name (required)
- `-m, --message`: Message to produce
- `-c, --consume`: Consume messages from topic
- `-g, --group-id`: Consumer group ID (default: kafka-cli-group)
- `-p, --produce`: Produce message to topic
- `-h, --help`: Show help message
- `-V, --version`: Show version information


## Usage

Setup consumer

```bash
make consume
```

Send message

```bash
MESSAGE=foo make produce
```


## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- Apache Kafka 3.6.1 or compatible version 
