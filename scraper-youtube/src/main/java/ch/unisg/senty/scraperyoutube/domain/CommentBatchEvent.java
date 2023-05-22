package ch.unisg.senty.scraperyoutube.domain;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CommentBatchEvent {
    private String videoId;
    private String timestamp;
    private List<CommentFetched> comments;
}
