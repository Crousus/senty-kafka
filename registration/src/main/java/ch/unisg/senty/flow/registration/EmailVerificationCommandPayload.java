package ch.unisg.senty.flow.registration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailVerificationCommandPayload {
    private String email;
    private String mailContent;
}
