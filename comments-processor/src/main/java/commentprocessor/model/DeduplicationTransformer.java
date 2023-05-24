package commentprocessor.model;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

public class DeduplicationTransformer implements Transformer<String, Comment, KeyValue<String, Comment>> {

    private KeyValueStore<String, Boolean> deduplicationStore;

    @Override
    public void init(ProcessorContext context) {
        this.deduplicationStore = (KeyValueStore<String, Boolean>) context.getStateStore("deduplication-store");
    }

    @Override
    public KeyValue<String, Comment> transform(String key, Comment value) {
        if (deduplicationStore.get(key) != null) {
            // Key has been seen before, filter it out.
            return null;
        } else {
            // Key has not been seen before, remember it and keep the record.
            deduplicationStore.put(key, true);
            return new KeyValue<>(key, value);
        }
    }

    @Override
    public void close() {}
}

