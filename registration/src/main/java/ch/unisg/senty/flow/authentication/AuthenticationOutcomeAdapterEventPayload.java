package ch.unisg.senty.flow.authentication;

import ch.unisg.senty.domain.Customer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationOutcomeAdapterEventPayload {
    private Customer customer;
    private boolean loginSuccessful;
}
