package ch.unisg.senty.flow.registration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegistrationSuccessfulEventPayload {

  private String customerId;
}
