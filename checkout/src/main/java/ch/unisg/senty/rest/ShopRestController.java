package ch.unisg.senty.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import ch.unisg.senty.repositoy.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.unisg.senty.domain.Order;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ShopRestController {
  
  @Autowired
  private MessageSender messageSender;

  @Autowired
  OrderRepository orderRepository;

  private static final Logger logger = LoggerFactory.getLogger(ShopRestController.class);


  @PostMapping(path = "/api/cart/order", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> placeOrder(@RequestBody Map<String, Object> requestBody) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      System.out.println(mapper.writeValueAsString(requestBody));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    Order order = new Order();

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
    } else if (order.getPassword().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Password is required\"}");
    }

    order.setVideoId("https://www.youtube.com/watch?v=" + order.getVideoId());
    Message<Order> message = new Message<Order>("OrderPlacedEvent", order);
    order.setOrderId(message.getTraceid());

    //save the order into db
    orderRepository.save(order);

    //kickoff workflow
    messageSender.send(message);

    //print message
    System.out.println(message.toString());

    logger.debug("MessageSend");

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  /*
    * This method is used to check the status of the order
    * @param traceId takes in the traceid as input
    * @return returns the status of the order
    */
  @GetMapping(path = "/api/cart/order/id/{traceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity checkOrderById(@PathVariable String traceId) {
    Optional<Order> order = orderRepository.findById(traceId);

    if (!order.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Order not found\"}");
    }
    Order presentOrder = order.get();
    return ResponseEntity.status(HttpStatus.OK).body(presentOrder);
  }

  @PostMapping(path = "/api/cart/order/ids", consumes =
          MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity checkOrdersByIds(@RequestBody List<String> traceIds) {
    List<Order> orders = new ArrayList<>();
    for (String traceId : traceIds) {
      Optional<Order> order = orderRepository.findById(traceId);
      if (!order.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Order not found for id: " + traceId + "\"}");
      }
      orders.add(order.get());
    }
    return ResponseEntity.status(HttpStatus.OK).body(orders);
  }

}