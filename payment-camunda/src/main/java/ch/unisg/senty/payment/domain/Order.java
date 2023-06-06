package ch.unisg.senty.payment.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Order implements java.io.Serializable {

    private String companyName;
    private String videoId;
    private String tokens;
    private String voucher;
    private String platform;

    private String email;

    private String password;
    private String status;

    public Map<String, Object> toMap() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("videoId", videoId);
        variables.put("tokens", tokens);
        variables.put("email", email);
        variables.put("password", password);
        variables.put("voucher", voucher);
        variables.put("platform", platform);
        variables.put("companyName", companyName);
        variables.put("status", status);
        return variables;

    }
}