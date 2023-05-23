package commentprocessor;

import commentprocessor.model.Comment;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.util.UUID;

import static commentprocessor.CommentProcessingTopology.predictLanguage;

public class RetryTransformerSupplier implements TransformerSupplier<byte[], Comment, KeyValue<byte[], Comment>> {
    private static final long RETRY_DELAY_MS = 20000; // 20 seconds

    private KeyValueStore<String, Comment> kvStore;

    @Override
    public Transformer<byte[], Comment, KeyValue<byte[], Comment>> get() {
        return new Transformer<byte[], Comment, KeyValue<byte[], Comment>>() {
            @Override
            public void init(ProcessorContext context) {
                kvStore = (KeyValueStore) context.getStateStore("inmemory-order-create-retry");

                context.schedule(Duration.ofMillis(RETRY_DELAY_MS), PunctuationType.WALL_CLOCK_TIME, timestamp -> {
                    try {
                        KeyValueIterator<String, Comment> iter = kvStore.all();
                        while (iter.hasNext()) {
                            KeyValue<String, Comment> entry = iter.next();
                            String language = predictLanguage(entry.value);
                            if (language != null) {
                                entry.value.setLanguage(language);
                                context.forward(entry.key, entry.value);
                                kvStore.delete(entry.key);
                            }
                        }
                        iter.close();
                        context.commit();
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                });
            }

            @Override
            public KeyValue<byte[], Comment> transform(byte[] key, Comment comment) {
                try {
                    String language = predictLanguage(comment);
                    comment.setLanguage(language);
                    return KeyValue.pair(key, comment);
                } catch (Exception e) {
                    String storeKey = System.currentTimeMillis() + "-" + UUID.randomUUID();
                    kvStore.put(storeKey, comment);
                    return null;
                }
            }

            @Override
            public void close() {}
        };
    }
}
