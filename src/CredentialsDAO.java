import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Base64;

public class CredentialsDAO {

    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom secureRandom = new SecureRandom();


    // 1. Save customer credentials
    public boolean saveCredentials(String customerID, Credentials credentials) {

        String sql = """
            INSERT INTO credentials
            (customer_id, username, password_hash)
            VALUES (?, ?, ?)
            """;

        if (customerID == null || credentials == null || credentials.getUsername() == null || credentials.getPassword() == null || credentials.getUsername().isBlank() || credentials.getPassword().isEmpty()) {

            System.out.println("Customer ID, username and password are required.");
            return false;
        }

        try {
            // Hash the password before storing it
            String passwordHash = hashPassword(credentials.getPassword());

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, customerID);
                statement.setString(2, credentials.getUsername());
                statement.setString(3, passwordHash);

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException | GeneralSecurityException e) {

            System.out.println(
                    "Failed to save credentials: " + e.getMessage()
            );

            return false;
        }
    }


    // 2. Authenticate a customer during login
    public boolean authenticate(String username, String password) {

        String sql = """
            SELECT password_hash
            FROM credentials
            WHERE username = ?
            """;

        if (username == null || password == null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    String storedHash = result.getString("password_hash");

                    return verifyPassword(password, storedHash);
                }
            }

        } catch (SQLException e) {

            System.out.println("Authentication failed: " + e.getMessage());
        }

        return false;
    }


    // 3. Check whether a username already exists
    public boolean usernameExists(String username) {

        String sql = """
            SELECT 1
            FROM credentials
            WHERE username = ?
            """;

        if (username == null || username.isBlank()) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                return result.next();
            }

        } catch (SQLException e) {

            System.out.println("Failed to check username: " + e.getMessage()
            );

            return false;
        }
    }


    // 4. Retrieve customer ID using username
    public String getCustomerIDByUsername(String username) {

        String sql = """
            SELECT customer_id
            FROM credentials
            WHERE username = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getString("customer_id");
                }
            }

        } catch (SQLException e) {

            System.out.println("Failed to retrieve customer ID: " + e.getMessage());
        }

        return null;
    }


    // 5. Generate a secure password hash
    private String hashPassword(String password) throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = generateHash(password, salt, ITERATIONS);

        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }


    // 6. Verify an entered password against the stored hash
    private boolean verifyPassword(String password, String storedHash) {

        try {
            String[] parts = storedHash.split(":");

            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);

            if (iterations < 1 || iterations > 1000000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[1]);

            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            byte[] enteredHash = generateHash(password, salt, iterations);

            return MessageDigest.isEqual(
                    expectedHash,
                    enteredHash
            );

        } catch (GeneralSecurityException | IllegalArgumentException e) {

            return false;
        }
    }


    // 7. Generate PBKDF2 hash
    private byte[] generateHash(String password, byte[] salt, int iterations) throws GeneralSecurityException {

        PBEKeySpec specification = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(specification).getEncoded();

        } finally {
            specification.clearPassword();
        }
    }
}

