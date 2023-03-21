package ch.unisg.senty.order.flow;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderSuccessfulEventPayload {

  private String videoId;
  private String customerId;

  private String tokens;

}
