package ch.unisg.senty.scraperyoutube.domain;

import lombok.ToString;

@ToString
public class CommentFetched {
    private String text;

    public CommentFetched(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
