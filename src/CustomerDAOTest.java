public class CustomerDAOTest {

    public static void main(String[] args) {

        // Initialize database
        DatabaseSetup.initializeDatabase();

        CustomerDAO customerDAO = new CustomerDAO();

        // Create customer
        Customer customer = new Customer("Fred", "Castro", "Gaborone", 71234567L);

        // Create credentials
        String username = "FCastro" + System.currentTimeMillis();
        String password = "Fred@123";

        Credentials credentials = new Credentials(username, password);
        customer.setCredentials(credentials);


        // TEST 1: SAVE CUSTOMER AND CREDENTIALS
        System.out.println("\n===== TEST 1: SAVE CUSTOMER AND CREDENTIALS =====");

        boolean saved = customerDAO.saveCustomer(customer);

        System.out.println("Saved successfully: " + saved);
        System.out.println("Customer ID: " + customer.getCustomerID());
        System.out.println("Username: " + username);


        if (!saved) {

            System.out.println("\nTesting stopped because registration failed.");
            return;
        }


        // TEST 2: RETRIEVE CUSTOMER
        System.out.println("\n===== TEST 2: RETRIEVE CUSTOMER =====");

        Customer retrievedCustomer = customerDAO.getCustomerByID(customer.getCustomerID());

        if (retrievedCustomer != null) {

            System.out.println("Retrieved successfully!");

            System.out.println("Customer ID: " + retrievedCustomer.getCustomerID());
            System.out.println("First Name: " + retrievedCustomer.getFirstName());
            System.out.println("Surname: " + retrievedCustomer.getSurname());
            System.out.println("Address: " + retrievedCustomer.getAddress());
            System.out.println("Contact: " + retrievedCustomer.getContactNo());

        } else {
            System.out.println("Customer could not be retrieved.");
        }


        // TEST 3: CHECK CUSTOMER EXISTS
        System.out.println("\n===== TEST 3: CUSTOMER EXISTS =====");

        boolean exists = customerDAO.customerExists(customer.getCustomerID());

        System.out.println("Customer exists: " + exists);


        // TEST 4: UPDATE CUSTOMER
        System.out.println("\n===== TEST 4: UPDATE CUSTOMER =====");

        customer.updateDetails("Timothy", "Simmons", "Francistown", 76543210L);

        boolean updated = customerDAO.updateCustomer(customer);

        System.out.println("Customer updated: " + updated);


        // TEST 5: VERIFY UPDATE
        System.out.println("\n===== TEST 5: VERIFY UPDATE =====");

        Customer updatedCustomer = customerDAO.getCustomerByID(customer.getCustomerID());

        if (updatedCustomer != null) {

            System.out.println("Updated first name: " + updatedCustomer.getFirstName());
            System.out.println("Updated surname: " + updatedCustomer.getSurname());
            System.out.println("Updated address: " + updatedCustomer.getAddress());
            System.out.println("Updated contact: " + updatedCustomer.getContactNo());

        }


        // TESTING COMPLETED
        System.out.println("\n===== CUSTOMER DAO TESTING COMPLETED =====");
    }
}