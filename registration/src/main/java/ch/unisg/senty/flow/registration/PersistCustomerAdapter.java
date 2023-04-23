package ch.unisg.senty.flow.registration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class PersistCustomerAdapter implements JavaDelegate {

    static final String JDBC_DRIVER = "org.h2.Driver";

    @Autowired
    private Environment env;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INSERT_CUSTOMER_SQL = "INSERT INTO customers(company, first_name, last_name, email, password, verified) VALUES (?, ?, ?, ?, ?, ?)";

    public PersistCustomerAdapter() {
        try {

            Class.forName(JDBC_DRIVER);
            // Create a database connection
            Connection conn = DriverManager.getConnection(env.getProperty("spring.datasource.url"), "sa", "");

            // Create a new statement
            PreparedStatement stmt = conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS customers ( id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    company TEXT NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    password TEXT NOT NULL,
                    verified BOOLEAN DEFAULT false)
                    """);

            // Execute the SQL query
            stmt.executeUpdate();
            // Close the statement and connection
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            logger.error("Error creating database", e);
        } catch (ClassNotFoundException e) {
            logger.error("Database driver not available", e);
        }
    }

    @Override
    public void execute(DelegateExecution execution) {

        WorkflowLogger.info(logger, "persistCustomer","Trying to insert customer into database");

        Customer customer = (Customer) execution.getVariable("customer");

        try {
            // Create a database connection
            Connection conn = DriverManager.getConnection(env.getProperty("spring.datasource.url"), "sa", "");

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(INSERT_CUSTOMER_SQL);
            stmt.setString(1, customer.getCompany());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPassword());
            stmt.setBoolean(6, (customer.isMailVerified() && customer.isHumanApproved()));

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
