package ch.unisg.senty.order.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Order {
  private String orderId;
  private String companyName;
  private String email;
  private String videoId;
  private String tokens;
  private String voucher;
  private String platform;
  private String password;
  private OrderStatus status;
  // private String paymentMethod;

  public Map<String, Object> toMap() {
    Map<String, Object> variables = new HashMap<>();
    variables.put("companyName", companyName);
    variables.put("email", email);
    variables.put("videoId", videoId);
    variables.put("tokens", tokens);
    variables.put("voucher", voucher);
    variables.put("platform", platform);
    return variables;

  }


}
