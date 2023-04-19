package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerRegistrationSuceededEventPayload {
    private Customer customer;
}
