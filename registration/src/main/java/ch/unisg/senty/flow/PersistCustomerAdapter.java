package ch.unisg.senty.flow;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("PersistCustomerAdapter")
public class PersistCustomerAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INSERT_CUSTOMER_SQL = "INSERT INTO customers(company, first_name, last_name, email, password) VALUES (?, ?, ?, ?, ?)";

    public PersistCustomerAdapter() {
        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection("jdbc:sqlite:customers.db");

            // Create a new statement
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS customers (id INTEGER PRIMARY KEY, company TEXT NOT NULL, first_name TEXT NOT NULL, last_name TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL)");

            // Execute the SQL query
            stmt.executeUpdate();
            // Close the statement and connection
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            logger.error("Error creating database", e);
        }
    }

    @Override
    public void execute(DelegateExecution execution) {

        WorkflowLogger.info(logger, "persistCustomer","Trying to insert customer into database");

        Customer customer = (Customer) execution.getVariable("customer");

        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection("jdbc:sqlite:customers.db");

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(INSERT_CUSTOMER_SQL);
            stmt.setString(1, customer.getCompany());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPassword());

            // Execute the SQL statement
            int rows = stmt.executeUpdate();

            WorkflowLogger.info(logger, "persistCustomer","Inserted " + rows +
                    " row(s) into the customers table");

            // Close the database connection
            conn.close();
        } catch (SQLException e) {
            logger.error("Error inserting customer into database", e);
        }
    }
}
