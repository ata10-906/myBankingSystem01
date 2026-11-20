import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerController {

    @FXML private TextField firstNameField;
    @FXML private TextField surnameField;
    @FXML private TextField addressField;
    @FXML private TextField contactField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label messageLabel;

    // Create CustomerDAO object
    private final CustomerDAO customerDAO = new CustomerDAO();


    @FXML
    private void registerCustomer() {

        try {

            //Get values from the text fields
            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String address = addressField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            //Validate that all fields are completed
            if (firstName.isEmpty() || surname.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty() || contactField.getText().trim().isEmpty()) {

                messageLabel.setText("Please complete all fields.");
                return;
            }

            //Convert contact number to long
            long contactNo = Long.parseLong(contactField.getText().trim());

            //Check username in the current application session
            if (AppSession.findCustomer(username) != null) {

                messageLabel.setText("Username already exists.");
                return;
            }

            //Create Customer object
            Customer customer = new Customer(firstName, surname, address, contactNo);

            //Create Credentials object
            Credentials credentials = new Credentials(username, password);
            //Attach credentials to customer
            customer.setCredentials(credentials);

            boolean saved = customerDAO.saveCustomer(customer);

            if (saved) {

                // Add customer to current application session
                AppSession.addCustomer(customer);
                messageLabel.setText("Customer registered successfully. ID: " + customer.getCustomerID());

            } else {
                messageLabel.setText("Registration failed. Username may already exist.");
            }


        } catch (NumberFormatException e) {
            messageLabel.setText("Contact number must be numeric.");
        }
    }


    @FXML
    private void goBack() throws Exception {
        ViewNavigator.show("LoginView.fxml");
    }
}