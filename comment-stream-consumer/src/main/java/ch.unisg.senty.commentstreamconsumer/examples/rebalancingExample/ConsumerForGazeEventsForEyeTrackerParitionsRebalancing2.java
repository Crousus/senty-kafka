package ch.unisg.senty.commentstreamconsumer.examples.rebalancingExample;

import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerForGazeEventsForEyeTrackerParitionsRebalancing2 {
    public static void main(String[] args) throws IOException, ParseException {

        // Read Kafka properties file and create Kafka consumer with the given properties
        KafkaConsumer<String, Object> consumer;
        try (InputStream props = Resources.getResource("consumer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            consumer = new KafkaConsumer<>(properties);
        }

        // subscribe to gaze-events topic
        consumer.subscribe(Arrays.asList("gaze-events"));


        while (true) {

            // pool new data
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(8));

            // process consumer records depending on record.topic() and record.value()
            for (ConsumerRecord<String, Object> record : records) {
                // switch/case
                switch (record.topic()) {
                    //note: record.value() is a linkedHashMap (see utils.JavaDeserializer), use can use the following syntax to access specific attributes ((LinkedHashMap) record.value()).get("ATTRIBUTENAME").toString(); The object can be also reconstructed as Gaze object
                    case "gaze-events":
                        String value =   record.value().toString();
                        System.out.println("Received gaze-events - key: " + record.key() +"- value: " + value + "- partition: "+record.partition());
                        break;

                    default:
                        throw new IllegalStateException("Shouldn't be possible to get message on topic " + record.topic());
                }
            }


        }
    }

}

