package commentprocessor;

import java.util.Properties;

import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  public static void main(String[] args) {
    Topology topology = CommentProcessingTopology.build();

    // we allow the following system properties to be overridden,
    // which allows us to run multiple instances of our app.
    // When running directly via IntelliJ, set additional parameters for the run configuration
    // VM Options: -Dhost=localhost -Dport=7000 -DstateDir=/tmp/kafka-streams
    String host = System.getProperty("host", "0.0.0.0");
    Integer port = Integer.parseInt(System.getProperty("port", "7002"));
    String stateDir = System.getProperty("stateDir", "/tmp/kafka-streams");
    String kafkaHost = System.getProperty("kafka", "localhost:29092");
    String endpoint = String.format("%s:%s", host, port);

    // set the required properties for running Kafka Streams
    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, endpoint);
    props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, CommentProcessingTopology.CommentTimestampExtractor.class.getName());


    // build the topology
    logger.debug("Starting Videogame Leaderboard");
    KafkaStreams streams = new KafkaStreams(topology, props);

    // close Kafka Streams when the JVM shuts down (e.g. SIGTERM)
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    // start streaming!
    streams.start();

    HostInfo hostInfo =  new HostInfo(host, port);
    CommentService service = new CommentService(hostInfo, streams);
    service.start();
  }
}
