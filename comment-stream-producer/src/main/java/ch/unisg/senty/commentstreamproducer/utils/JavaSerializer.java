package ch.unisg.senty.commentstreamproducer.utils;


import ch.unisg.senty.commentstreamproducer.domain.Comment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/* Java Serializer adapted from https://www.baeldung.com/kafka-custom-serializer */

public class JavaSerializer implements Serializer<Comment> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Comment data) {
        try {
            if (data == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Comment " +
                    "to byte[]");
        }
    }

    @Override
    public void close() {
    }

}
