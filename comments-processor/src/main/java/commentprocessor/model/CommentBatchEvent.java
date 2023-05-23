package commentprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CommentBatchEvent {

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("comments")
    private List<Comment> comments;
}
