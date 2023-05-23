package commentprocessor.serialization.json;

import commentprocessor.model.*;
import commentprocessor.model.join.Enriched;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

  public static Serde<Enriched> Enriched() {
    JsonSerializer<Enriched> serializer = new JsonSerializer<>();
    JsonDeserializer<Enriched> deserializer = new JsonDeserializer<>(Enriched.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<CommentBatchEvent> CommentBatch() {
    JsonSerializer<CommentBatchEvent> serializer = new JsonSerializer<>();
    JsonDeserializer<CommentBatchEvent> deserializer = new JsonDeserializer<>(CommentBatchEvent.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<Comment> Comment() {
    JsonSerializer<Comment> serializer = new JsonSerializer<>();
    JsonDeserializer<Comment> deserializer = new JsonDeserializer<>(Comment.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<Player> Player() {
    JsonSerializer<Player> serializer = new JsonSerializer<>();
    JsonDeserializer<Player> deserializer = new JsonDeserializer<>(Player.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<Product> Product() {
    JsonSerializer<Product> serializer = new JsonSerializer<>();
    JsonDeserializer<Product> deserializer = new JsonDeserializer<>(Product.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }
}
