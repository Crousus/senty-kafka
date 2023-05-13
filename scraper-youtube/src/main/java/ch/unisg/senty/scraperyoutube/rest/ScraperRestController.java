package ch.unisg.senty.scraperyoutube.rest;

import ch.unisg.senty.scraperyoutube.messages.Message;
import ch.unisg.senty.scraperyoutube.messages.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScraperRestController {
  
  @Autowired
  private MessageSender messageSender;
  
  @GetMapping(path = "/api/scraperyoutube/ping")
  public ResponseEntity<String> ping() {
    Message message = new Message("PingYouTubeScraperCommand");

    messageSender.send(message);

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/verify")
  public ResponseEntity<String> verify(@RequestParam String url) {
    Message<String> message = new Message<String>("VerifyUrlCommand", url);

    messageSender.send(message);
    
    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/fetch")
  public ResponseEntity<String> fetch(@RequestParam String videoId) {
    Message<String> message = new Message<String>("FetchCommentsCommand",
            videoId);

    messageSender.send(message);

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/remove")
  public ResponseEntity<String> remove(@RequestParam String videoId) {
    Message<String> message = new Message<String>("RemoveVideoIdCommand",
            videoId);

    messageSender.send(message);

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }
}