package ch.unisg.senty.scraperyoutube.rest;

import ch.unisg.senty.scraperyoutube.messages.Message;
import ch.unisg.senty.scraperyoutube.messages.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopRestController {
  
  @Autowired
  private MessageSender messageSender;
  
  @PostMapping(path = "/api/cart/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> placeOrder() {
    Message<String> message = new Message<String>("OrderPlacedEvent", "moin");

    messageSender.send(message);

    System.out.println("MessageSend");

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }
}