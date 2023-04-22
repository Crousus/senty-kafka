package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class DeleteCustomerAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DELETE_CUSTOMER_SQL = "DELETE FROM customers WHERE email = ? AND verified = false";

    @Override
    public void execute(DelegateExecution execution) {

        WorkflowLogger.info(logger, "persistCustomer","Trying to insert customer into database");

        Customer customer = (Customer) execution.getVariable("customer");

        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(DELETE_CUSTOMER_SQL);
            stmt.setString(1, customer.getEmail());

            // Execute the SQL statement
            int rows = stmt.executeUpdate();

            WorkflowLogger.info(logger, "deleteCustomer","Removed " + rows +
                    " row(s) from the customers table");

            // Close the database connection
            conn.close();
        } catch (SQLException e) {
            logger.error("Error removing customer from database", e);
        }
    }
}
