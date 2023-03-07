package ch.unisg.senty.projectmanager.domain;

import lombok.Data;

import java.util.List;

@Data
public class Post {

    private String channelName;
    private String videoId;
    private List<String> scrapers;
}
