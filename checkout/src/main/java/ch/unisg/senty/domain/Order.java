package ch.unisg.senty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Order {
  private String companyName;
  private String email;
  private String videoId;
  private String tokens;
  private String voucher;
  private String platform;

  private String password;
  // private String paymentMethod;
}
