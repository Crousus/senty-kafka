package commentprocessor.model;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.UUID;

import static commentprocessor.CommentProcessingTopology.predictLanguage;

public class RetryProcessor implements Processor<byte[], Comment> {

    private ProcessorContext context;
    private KeyValueStore<String, RetryMessage<Comment>> kvStore;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        this.kvStore = (KeyValueStore) context.getStateStore("inmemory-order-create-retry");

        context.schedule(20000, PunctuationType.WALL_CLOCK_TIME, (ts) -> {
            try {
                KeyValueIterator<String, RetryMessage<Comment>> iter = kvStore.all();
                while (iter.hasNext()) {
                    KeyValue<String, RetryMessage<Comment>> entry = iter.next();

                    if (System.currentTimeMillis() >= entry.value.getRetryTime()) {
                        context.forward(entry.key, entry.value.getValue());
                        kvStore.delete(entry.key);
                    }
                }
                iter.close();

                context.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void process(byte[] key, Comment value) {
        try {
            String language = predictLanguage(value);
            System.out.println("Language: " + language);
            value.setLanguage(language);

            if (!"en".equalsIgnoreCase(language)) {
                long retryTime = System.currentTimeMillis() + 20000;  // Calculate the retry timestamp here.
                RetryMessage<Comment> retryMessage = new RetryMessage<>(retryTime, value);

                String storeKey = String.valueOf(retryTime) + "-" + UUID.randomUUID().toString();

                kvStore.put(storeKey, retryMessage);  // Store the retry message instead of raw value.
            } else {
                context.forward(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {}
}
