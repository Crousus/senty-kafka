package ch.unisg.senty.order.flow.orderplaced;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationRequestCommandPayload {
    private String email;
    private String password;
}
