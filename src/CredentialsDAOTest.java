import java.util.UUID;

public class CredentialsDAOTest {

    public static void main(String[] args) {

        CustomerDAO customerDAO = new CustomerDAO();
        CredentialsDAO credentialsDAO = new CredentialsDAO();

        // Create a test customer first
        Customer customer = new Customer("David", "Molefe", "Gaborone", 71234567L);

        System.out.println("===== TEST 1: SAVE CUSTOMER =====");

        boolean customerSaved = customerDAO.saveCustomer(customer);
        System.out.println("Customer saved: " + customerSaved);

        if (!customerSaved) {
            System.out.println("Cannot continue: customer was not saved.");
            return;
        }

        // Create unique test credentials
        String username = "david_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "BankTest123!";

        Credentials credentials = new Credentials(username, password);


        // TEST 2: SAVE CREDENTIALS
        System.out.println("\n===== TEST 2: SAVE CREDENTIALS =====");

        boolean credentialsSaved = credentialsDAO.saveCredentials(customer.getCustomerID(), credentials);
        System.out.println("Credentials saved: " + credentialsSaved);


        // TEST 3: CHECK USERNAME
        System.out.println("\n===== TEST 3: CHECK USERNAME =====");

        boolean exists = credentialsDAO.usernameExists(username);
        System.out.println("Username exists: " + exists);


        // TEST 4: CORRECT PASSWORD
        System.out.println("\n===== TEST 4: VALID LOGIN =====");

        boolean validLogin = credentialsDAO.authenticate(username, password);
        System.out.println("Login successful: " + validLogin);


        // TEST 5: INCORRECT PASSWORD
        System.out.println("\n===== TEST 5: INVALID LOGIN =====");

        boolean invalidLogin = credentialsDAO.authenticate(username, "WrongPassword123");
        System.out.println("Login successful: " + invalidLogin);


        // TEST 6: RETRIEVE CUSTOMER ID
        System.out.println("\n===== TEST 6: RETRIEVE CUSTOMER ID =====");

        String retrievedCustomerID = credentialsDAO.getCustomerIDByUsername(username);
        System.out.println("Original customer ID: " + customer.getCustomerID());
        System.out.println("Retrieved customer ID: " + retrievedCustomerID);

        System.out.println("Customer IDs match: " + customer.getCustomerID().equals(retrievedCustomerID));
    }
}

