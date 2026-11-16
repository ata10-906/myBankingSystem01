import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Enter username and password.");
            return;
        }

        //Find the customer using their usernames.
        Customer customer = AppSession.findCustomer(username);

        if  (customer == null) {
            messageLabel.setText("Customer not found.");
            return;
        }

        //Get the customer's login credentials.
        Credentials credentials = customer.getCredentials();

        //Verify the entered password.
        if (credentials.verify(password)) {
            AppSession.setCurrentCustomer(customer);

            try {
                ViewNavigator.show("DashBoardView.fxml");
            } catch (Exception e) {
                messageLabel.setText("Unable to open dashboard.");
            }
        } else {
            messageLabel.setText("Incorrect password.");
        }
    }


    @FXML private void initialize() {
        messageLabel.setText("Please login to continue.");
    }

}
