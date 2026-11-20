import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // Connect to the database through CustomerDAO
    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {

            messageLabel.setText("Enter username and password.");
            return;
        }

        Customer customer = customerDAO.authenticateCustomer(username, password);

        // Check authentication result
        if (customer == null) {
            messageLabel.setText("Incorrect username or password.");
            return;
        }

        // Authentication successful
        AppSession.setCurrentCustomer(customer);

        // Open customer dashboard
        try {

            ViewNavigator.show("DashBoardView.fxml");

        } catch (Exception e) {

            messageLabel.setText("Unable to open dashboard.");
            System.out.println("Dashboard error: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        messageLabel.setText("Please login to continue.");
    }
}