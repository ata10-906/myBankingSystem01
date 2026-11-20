import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // SQLite database file
    private static final String URL = "jdbc:sqlite:banking_system.db";

    // Establish database connection
    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(URL);

        try (Statement statement = connection.createStatement()) {

            // Enable foreign-key constraints
            statement.execute("PRAGMA foreign_keys = ON");

            return connection;

        } catch (SQLException e) {

            // Close connection if setup fails
            connection.close();

            throw e;
        }
    }
}


