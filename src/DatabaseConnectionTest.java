import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            if (connection != null && !connection.isClosed()) {

                System.out.println(
                        "Database connected successfully!"
                );

                // Test foreign-key enforcement
                try (Statement statement =
                             connection.createStatement();
                     ResultSet result =
                             statement.executeQuery(
                                     "PRAGMA foreign_keys"
                             )) {

                    if (result.next()) {

                        int foreignKeysEnabled =
                                result.getInt(1);

                        if (foreignKeysEnabled == 1) {
                            System.out.println(
                                    "Foreign-key enforcement: ENABLED"
                            );
                        } else {
                            System.out.println(
                                    "Foreign-key enforcement: DISABLED"
                            );
                        }
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Database connection failed: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}

