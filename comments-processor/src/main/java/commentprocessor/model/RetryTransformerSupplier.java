package commentprocessor.model;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import static commentprocessor.CommentProcessingTopology.predictLanguage;

public class RetryTransformerSupplier implements TransformerSupplier<String, Comment, KeyValue<String, Comment>> {

    @Override
    public Transformer<String, Comment, KeyValue<String, Comment>> get() {
        return new Transformer<>() {
            @Override
            public void init(ProcessorContext context) {
            }

            @Override
            public KeyValue<String, Comment> transform(String key, Comment comment) {
                try {
                    String language = predictLanguage(comment);
                    comment.setLanguage(language);
                    System.out.println("Predicted: " + language);
                    return KeyValue.pair(key, comment);
                } catch (Exception e) {
                    return KeyValue.pair(key, comment);
                }
            }

            @Override
            public void close() {
            }
        };
    }
}
