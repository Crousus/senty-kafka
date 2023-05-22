package ch.unisg.senty.scraperyoutube.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class CommentFetched {
    private String id;
    private String textDisplay;
    private String textOriginal;
    private String authorDisplayName;
    private String authorProfileImageUrl;
    private String authorChannelUrl;
    private String authorChannelId;
    private int likeCount;
    private String publishedAt;
    private String updatedAt;
}
