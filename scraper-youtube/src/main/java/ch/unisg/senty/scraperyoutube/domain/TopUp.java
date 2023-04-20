package ch.unisg.senty.scraperyoutube.domain;

import lombok.Data;

@Data
public class TopUp {
    private String customerId;
    private String videoId;
    private int tokens;
}
