import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Base64;
import java.util.List;

public class CustomerDAO {

    // 1. SAVE CUSTOMER AND CREDENTIALS TO DATABASE
    public boolean saveCustomer(Customer customer) {

        String customerSQL = """
            INSERT INTO customers
            (customer_id, first_name, surname, address, contact_no)
            VALUES (?, ?, ?, ?, ?)
            """;

        String credentialsSQL = """
            INSERT INTO credentials
            (customer_id, username, password_hash)
            VALUES (?, ?, ?)
            """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();

            // Begin transaction
            connection.setAutoCommit(false);


            // Save customer details
            try (PreparedStatement customerStatement = connection.prepareStatement(customerSQL)) {

                customerStatement.setString(1, customer.getCustomerID());
                customerStatement.setString(2, customer.getFirstName());
                customerStatement.setString(3, customer.getSurname());
                customerStatement.setString(4, customer.getAddress());
                customerStatement.setLong(5, customer.getContactNo());

                customerStatement.executeUpdate();
            }


            // Save credentials
            Credentials credentials = customer.getCredentials();

            if (credentials == null) {
                throw new SQLException("Customer credentials are missing.");
            }

            String passwordHash = hashPassword(credentials.getPassword());

            try (PreparedStatement credentialsStatement = connection.prepareStatement(credentialsSQL)) {

                credentialsStatement.setString(1, customer.getCustomerID());
                credentialsStatement.setString(2, credentials.getUsername());
                credentialsStatement.setString(3, passwordHash);

                credentialsStatement.executeUpdate();
            }

            // Both operations succeeded
            connection.commit();

            System.out.println("Customer and credentials saved successfully!");
            return true;

        } catch (Exception e) {

            // Undo both insertions if anything failed
            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    System.out.println("Rollback failed: " + rollbackException.getMessage());
                }
            }

            System.out.println("Failed to save customer: " + e.getMessage());
            return false;

        } finally {

            if (connection != null) {

                try {
                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {
                    System.out.println("Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }



    // 2. RETRIEVE CUSTOMER BY ID

    public Customer getCustomerByID(String customerID) {

        String sql = """
            SELECT customer_id, first_name, surname, address, contact_no
            FROM customers
            WHERE customer_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Customer(
                            result.getString("customer_id"),
                            result.getString("first_name"),
                            result.getString("surname"),
                            result.getString("address"),
                            result.getLong("contact_no")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve customer: " + e.getMessage());
        }

        return null;
    }


    // 3. UPDATE CUSTOMER DETAILS
    public boolean updateCustomer(Customer customer) {

        String sql = """
            UPDATE customers
            SET first_name = ?,
                surname = ?,
                address = ?,
                contact_no = ?
            WHERE customer_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getSurname());
            statement.setString(3, customer.getAddress());
            statement.setLong(4, customer.getContactNo());
            statement.setString(5, customer.getCustomerID());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Failed to update customer: " + e.getMessage());
            return false;
        }
    }


    // 4. CHECK WHETHER CUSTOMER EXISTS
    public boolean customerExists(String customerID) {

        String sql = """
            SELECT 1
            FROM customers
            WHERE customer_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerID);

            try (ResultSet result = statement.executeQuery()) {

                return result.next();
            }

        } catch (SQLException e) {
            System.out.println("Failed to check customer: " + e.getMessage());
            return false;
        }
    }


    // 5. AUTHENTICATE CUSTOMER USING DATABASE
    public Customer authenticateCustomer(String username, String password) {

        String sql = """
        SELECT c.customer_id, c.first_name, c.surname,
               c.address, c.contact_no, cr.password_hash
        FROM customers c
        JOIN credentials cr
        ON c.customer_id = cr.customer_id
        WHERE cr.username = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    String storedPasswordHash = result.getString("password_hash");

                    // Verify the entered password
                    if (verifyPassword(password, storedPasswordHash)) {

                        // Retrieve customer information
                        Customer customer = new Customer(
                                result.getString("customer_id"),
                                result.getString("first_name"),
                                result.getString("surname"),
                                result.getString("address"),
                                result.getLong("contact_no")
                        );


                        // Retrieve the customer's existing accounts from the database
                        AccountDAO accountDAO = new AccountDAO();

                        List<Account> accounts = accountDAO.getAccountsByCustomerID(customer.getCustomerID());

                        // Attach the retrieved accounts to the customer
                        for (Account account : accounts) {
                            customer.addAccount(account);
                        }

                        System.out.println("Customer accounts loaded: " + accounts.size());
                        return customer;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }

        return null;
    }


    // 6. VERIFY PASSWORD
    private boolean verifyPassword(String password, String storedPasswordHash) throws Exception {

        // Separate salt and hash
        String[] parts = storedPasswordHash.split(":");

        String saltString = parts[0];
        String hashString = parts[1];

        // Decode Base64 values
        byte[] salt = Base64.getDecoder().decode(saltString);
        byte[] storedHash = Base64.getDecoder().decode(hashString);

        // Use the same settings as hashPassword()
        int iterations = 120000;
        int keyLength = 256;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] enteredPasswordHash = factory.generateSecret(spec).getEncoded();

        // Compare entered password hash with stored hash
        return java.security.MessageDigest.isEqual(enteredPasswordHash, storedHash);
    }


    // 7. HASH PASSWORD
    private String hashPassword(String password) throws Exception {

        // Generate random salt
        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];

        random.nextBytes(salt);

        // Password hashing settings
        int iterations = 120000;
        int keyLength = 256;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Convert salt and hash into Base64 strings
        String saltString = Base64.getEncoder().encodeToString(salt);

        String hashString = Base64.getEncoder().encodeToString(hash);

        // Store salt and hash together
        return saltString + ":" + hashString;
    }

}