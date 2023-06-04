package commentprocessor.model;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static commentprocessor.CommentProcessingTopology.predictLanguage;

public class RetryTransformerSupplier implements TransformerSupplier<String, Comment, KeyValue<String, Comment>> {

    private static final Logger logger = LoggerFactory.getLogger(RetryTransformerSupplier.class);
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
                    logger.debug("Predicted: " + language);
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
