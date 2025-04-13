package com.kafka;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Command(name = "kafka-cli", mixinStandardHelpOptions = true, version = "1.0",
        description = "A CLI tool for Kafka operations")
public class KafkaCli implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(KafkaCli.class);

    @Option(names = {"-b", "--bootstrap-servers"}, description = "Kafka bootstrap servers", required = true)
    private String bootstrapServers;

    @Option(names = {"-t", "--topic"}, description = "Kafka topic name", required = true)
    private String topic;

    @Option(names = {"-m", "--message"}, description = "Message to produce")
    private String message;

    @Option(names = {"-c", "--consume"}, description = "Consume messages from topic")
    private boolean consume;

    @Option(names = {"-g", "--group-id"}, description = "Consumer group ID")
    private String groupId = "kafka-cli-group";

    @Option(names = {"-p", "--produce"}, description = "Produce message to topic")
    private boolean produce;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new KafkaCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            if (produce && message != null) {
                produceMessage();
            } else if (consume) {
                consumeMessages();
            } else {
                System.out.println("Please specify either --produce with a message or --consume");
            }
        } catch (Exception e) {
            logger.error("Error executing Kafka operation", e);
            System.exit(1);
        }
    }

    private void produceMessage() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
            RecordMetadata metadata = producer.send(record).get();
            System.out.printf("Message sent to topic %s, partition %d, offset %d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error producing message", e);
            throw new RuntimeException(e);
        }
    }

    private void consumeMessages() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            System.out.println("Starting to consume messages from topic: " + topic);
            
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received message: topic = %s, partition = %d, offset = %d, key = %s, value = %s%n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
            }
        }
    }
} 