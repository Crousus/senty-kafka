package commentprocessor.serialization.json;

import commentprocessor.Languages;
import commentprocessor.model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

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

  public static Serde<Languages> Languages() {
    JsonSerializer<Languages> serializer = new JsonSerializer<>();
    JsonDeserializer<Languages> deserializer = new JsonDeserializer<>(Languages.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<RecentComments> RecentComments() {
    JsonSerializer<RecentComments> serializer = new JsonSerializer<>();
    JsonDeserializer<RecentComments> deserializer = new JsonDeserializer<>(RecentComments.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<Boolean> Boolean() {
    JsonSerializer<Boolean> serializer = new JsonSerializer<>();
    JsonDeserializer<Boolean> deserializer = new JsonDeserializer<>(Boolean.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }
}
