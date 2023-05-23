package commentprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString
public class Comment {

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("comment_id")
    private String commentId = UUID.randomUUID().toString();

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("sentiment_score")
    private double sentimentScore;

    @JsonProperty("language")
    private String language;

    @JsonProperty("sub_comments")
    private List<String> subComments;
}
