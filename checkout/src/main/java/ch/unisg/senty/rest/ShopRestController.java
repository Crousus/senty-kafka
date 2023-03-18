package ch.unisg.senty.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.domain.Order;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;

@RestController
public class ShopRestController {
  
  @Autowired
  private MessageSender messageSender;
  
  @RequestMapping(path = "/api/cart/order", method = POST)
  public String placeOrder() {
    
    Order order = new Order();
    order.addItem("article1", 5);
    order.addItem("article2", 10);
    
    order.setCustomer(new Customer("Camunda", "Zossener Strasse 55\n10961 Berlin\nGermany"));
    
    Message<Order> message = new Message<Order>("OrderPlacedEvent", order);
    messageSender.send(message);
        
    // note that we cannot easily return an order id here - as everything is asynchronous
    // and blocking the client is not what we want.
    // but we return an own correlationId which can be used in the UI to show status maybe later
    return "{\"traceId\": \"" + message.getTraceid() + "\"}";
  }

}