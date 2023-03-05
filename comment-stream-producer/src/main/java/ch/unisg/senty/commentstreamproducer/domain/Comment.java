package ch.unisg.senty.commentstreamproducer.domain;

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

    public Comment(String channelId, String channelName, String videoId, String videoName, String commentAuthorId,
                   String commentAuthorName, String commentId, String commentText, String commentDateTime, long timeDiffSec) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.videoId = videoId;
        this.videoName = videoName;
        this.commentAuthorId = commentAuthorId;
        this.commentAuthorName = commentAuthorName;
        this.commentId = commentId;
        this.commentText = commentText;
        this.commentDateTime = commentDateTime;
        this.timeDiffSec = timeDiffSec;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getCommentAuthorId() {
        return commentAuthorId;
    }

    public String getCommentAuthorName() {
        return commentAuthorName;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentDateTime() {
        return commentDateTime;
    }

    public long getTimeDiffSec() {
        return timeDiffSec;
    }

    public String toString() {
        return "NEW COMMENT [channelId: " + channelId + ", " +
                "channelName: " + channelName + ", " +
                "videoId: " + videoId + ", " +
                "videoName: " + videoName + ", " +
                "commentAuthorId: " + commentAuthorId + ", " +
                "commentAuthorName: " + commentAuthorName + ", " +
                "commentId: " + commentId + ", " +
                "commentText: " + commentText + ", " +
                "commentDateTime: " + commentDateTime + ", " +
                "timeDiffSec: " + timeDiffSec + "]";
    }
}
