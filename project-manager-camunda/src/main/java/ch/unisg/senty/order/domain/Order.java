package ch.unisg.senty.order.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Order {
  private String customerId;
  private String videoId;
  private String tokens;

  private String email;

  private String password;
  // private String paymentMethod;

  public Map<String, Object> toMap() {
    Map<String, Object> variables = new HashMap<>();
    variables.put("customerId", customerId);
    variables.put("videoId", videoId);
    variables.put("tokens", tokens);
    variables.put("email", email);
    variables.put("password", password);
    return variables;

  }
}
