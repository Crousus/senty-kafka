package ch.unisg.senty.scraperyoutube.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
