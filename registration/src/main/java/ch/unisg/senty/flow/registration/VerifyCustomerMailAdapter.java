package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.MessageSender;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


@Service
public class VerifyCustomerMailAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSender messageSender;

    private static final String UPDATE_CUSTOMER_SQL = "UPDATE customers SET verified = true WHERE email = ?";

    @Override
    public void execute(DelegateExecution execution) {

        WorkflowLogger.info(logger, "updateCustomer","Trying to update customer into verfied status");

        Customer customer = (Customer) execution.getVariable("customer");

        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(UPDATE_CUSTOMER_SQL);
            stmt.setString(1, customer.getEmail());

            // Execute the SQL statement
            int rows = stmt.executeUpdate();

            WorkflowLogger.info(logger, "updateCustomer","Updated " + rows +
                    " row(s) in the customers table");

            // Close the database connection
            conn.close();
        } catch (SQLException e) {
            logger.error("Error updating customer from database", e);
        }
    }
}
