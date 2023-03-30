package ch.unisg.senty.flow;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegistrationSuccessfulEventPayload {

  private String customerId;
}
