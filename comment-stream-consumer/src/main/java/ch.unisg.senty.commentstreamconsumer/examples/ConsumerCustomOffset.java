package ch.unisg.senty.commentstreamconsumer.examples;


import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerCustomOffset {
    public static void main(String[] args) throws IOException, ParseException {

        // Read Kafka properties file and create Kafka consumer with the given properties
        KafkaConsumer<String, Object> consumer;
        try (InputStream props = Resources.getResource("consumer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            consumer = new KafkaConsumer<>(properties);
        }

        // Read specific topic and partition
        TopicPartition topicPartition = new TopicPartition("click-events", 0);
        consumer.assign(Arrays.asList(topicPartition));

        // reading from a specific user defined offset
        int offsetToReadFrom = 5;
        consumer.seek(topicPartition, offsetToReadFrom);


        while (true) {


            // pool new data
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(500));


            // process consumer records depending on record.topic() and record.value()
            for (ConsumerRecord<String, Object> record : records) {
                // switch/case
                switch (record.topic()) {

                    case "click-events":
                        System.out.println("Received click-events - value: " + record.value());

                        break;

                    default:
                        throw new IllegalStateException("Shouldn't be possible to get message on topic " + record.topic());
                }

            }




        }
    }

}

