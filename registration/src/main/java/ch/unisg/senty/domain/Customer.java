package ch.unisg.senty.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Customer implements Serializable {
    private String company;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean mailVerified;
    private boolean humanApproved;

    public Map<String, Object> toMap() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("company", company);
        variables.put("firstName", firstName);
        variables.put("lastName", lastName);
        variables.put("email", email);
        variables.put("password", password);
        variables.put("mailVerified", mailVerified);
        return variables;
    }
}
