package ch.unisg.senty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "orders")
public class Order {

  @Id
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
}
