package ch.unisg.senty.commentanalysis.domain;

import lombok.Data;

@Data
public class Comment {

    String channelId;
    String channelName;
    String videoId;
    String videoName;
    String commentAuthorId;
    String commentAuthorName;
    String commentId;
    String commentText;
    String commentDateTime;
    long timeDiffSec;
}
