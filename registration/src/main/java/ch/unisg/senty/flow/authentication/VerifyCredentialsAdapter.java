package ch.unisg.senty.flow.authentication;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;

@Service
public class VerifyCredentialsAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CHECK_CUSTOMER_SQL = "SELECT * FROM customers WHERE email = ? AND password = ? AND verified = true";

    @Override
    public void execute(DelegateExecution context) {

        WorkflowLogger.info(logger, "persistCustomer", "Check customer credentials");

        Customer customer = (Customer) context.getVariable("customer");


        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(CHECK_CUSTOMER_SQL);
            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPassword());

            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer.setCompany(rs.getString("company"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setMailVerified(rs.getBoolean("verified"));
                customer.setHumanApproved(rs.getBoolean("verified"));

                context.setVariable("customer", customer);
                context.setVariable("loginSuccessful", true);
                WorkflowLogger.info(logger, "Login", "Found " + customer +
                        " row(s) from the customers table");
            } else {
                context.setVariable("loginSuccessful", false);

                WorkflowLogger.info(logger, "Login", "failed " + customer +
                        " row(s) from the customers table");
            }

            // Close the database connection
            conn.close();
        } catch (SQLException e) {
            logger.error("Error removing customer from database", e);
        }
    }
}
