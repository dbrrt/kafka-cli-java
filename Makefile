# Kafka CLI Makefile

# Variables
JAR_FILE = target/kafka-cli-1.0-SNAPSHOT-jar-with-dependencies.jar
BOOTSTRAP_SERVERS ?= localhost:9092
TOPIC ?= my-topic
GROUP_ID ?= kafka-cli-group

# Default target
.PHONY: all
all: build

# Build the project
.PHONY: build
build:
	mvn clean package

# Clean the project
.PHONY: clean
clean:
	mvn clean

# Run the help command
.PHONY: help
help:
	java -jar $(JAR_FILE) --help

# Produce a message to a topic
.PHONY: produce
produce:
	@if [ -z "$(MESSAGE)" ]; then \
		echo "Error: MESSAGE is required. Usage: make produce MESSAGE=\"your message\" TOPIC=your-topic"; \
		exit 1; \
	fi
	java -jar $(JAR_FILE) \
		--bootstrap-servers $(BOOTSTRAP_SERVERS) \
		--topic $(TOPIC) \
		--produce \
		--message "$(MESSAGE)"

# Consume messages from a topic
.PHONY: consume
consume:
	java -jar $(JAR_FILE) \
		--bootstrap-servers $(BOOTSTRAP_SERVERS) \
		--topic $(TOPIC) \
		--consume \
		--group-id $(GROUP_ID)

# Run with custom bootstrap servers
.PHONY: run
run:
	java -jar $(JAR_FILE) $(ARGS)

# Show version information
.PHONY: version
version:
	java -jar $(JAR_FILE) --version

# Example usage instructions
.PHONY: examples
examples:
	@echo "Kafka CLI Examples:"
	@echo "  make build                                    # Build the project"
	@echo "  make produce MESSAGE=\"Hello, Kafka!\"         # Produce a message"
	@echo "  make consume TOPIC=my-topic                   # Consume messages"
	@echo "  make run ARGS=\"--bootstrap-servers kafka:9092 --topic test --consume\"  # Run with custom args"
	@echo "  make help                                     # Show help"
	@echo "  make version                                  # Show version" 