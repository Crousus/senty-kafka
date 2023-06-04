package commentprocessor.model;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.To;

import java.time.Instant;

public class OverrideCommentTimestampTransformer implements  Transformer<String, Comment, KeyValue<String, Comment>> {
    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, Comment> transform(String key, Comment value) {
        // Modify the timestamp here
        context.forward(key, value, To.all().withTimestamp(Instant.parse(value.getTimestamp()).toEpochMilli()));

        return null;
    }

    @Override
    public void close() {
    }
}
