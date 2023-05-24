package ch.unisg.senty.scraperyoutube.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@Data
public class CommentFetched {

    @JsonProperty("comment_id")
    private String commentId;

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("comment")
    private String comment;
    //private String authorDisplayName;
    //private String authorProfileImageUrl;
    //private String authorChannelUrl;
    //private String authorChannelId;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("subComments")
    private List<CommentFetched> subComments;

}
