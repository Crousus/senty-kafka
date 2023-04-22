package ch.unisg.senty.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.unisg.senty.domain.Order;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;

import java.util.UUID;

@RestController
public class ShopRestController {
  
  @Autowired
  private MessageSender messageSender;
  
  @PostMapping(path = "/api/cart/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> placeOrder(@RequestBody Order order) {
    if (order.getCompanyName().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Company Name is required\"}");
    } else if (order.getEmail().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Customer ID is required\"}");
    } else if (order.getVideoId().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Video ID is required\"}");
    } else if (order.getTokens().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Tokens are required\"}");
    } else if (order.getPlatform().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Platform is required\"}");
    }

    Message<Order> message = new Message<Order>("OrderPlacedEvent", order);

    messageSender.send(message);

    System.out.println("MessageSend");

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }
}