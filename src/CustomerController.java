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

    @FXML private void registerCustomer() {

        try {
            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String address = addressField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (firstName.isEmpty() || surname.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty() || contactField.getText().trim().isEmpty()) {
                messageLabel.setText("Please complete all fields.");
                return;
            }

            long contactNo = Long.parseLong(contactField.getText().trim());

            if (AppSession.findCustomer(username) != null) {
                messageLabel.setText("Username already exists.");
                return;
            }

            //Create a customer using the existing constructor.
            Customer customer = new Customer(firstName, surname, address, contactNo);

            //Create login credentials
            Credentials credentials = new Credentials(username, password);

            customer.setCredentials(credentials);

            //Store the registered customer.
            AppSession.addCustomer(customer);

            messageLabel.setText("Customer registered successfully. ID: " + customer.getCustomerID());
        } catch (NumberFormatException e) {
            messageLabel.setText("Contact number must be numeric.");
        }
    }

    @FXML private void goBack() throws Exception {
        ViewNavigator.show("LoginView.fxml");
    }
}
