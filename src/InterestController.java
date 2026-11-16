import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class InterestController {

    @FXML private Label messageLabel;

    private InterestBearing getInterestAccount() {
        Account account = AppSession.getSelectedAccount();

        if (account instanceof InterestBearing) {
            return (InterestBearing) account;
        }

        messageLabel.setText("Select a Savings or Investment Account.");
        return null;
    }

    @FXML private void showInterestRate() {
        InterestBearing account = getInterestAccount();

        if (account != null) {
            messageLabel.setText(String.format("Monthly Interest Rate: BWP %.2f", account.getInterestRate() * 100));
        }
    }

    @FXML private void calculateInterest() {
        InterestBearing account = getInterestAccount();

        if (account != null) {
            messageLabel.setText(String.format("Calculated Interest: BWP %.2f", account.calculateInterest()));
        }
    }

    @FXML private void processInterest() {
        InterestBearing account  = getInterestAccount();

        if (account != null) {
            messageLabel.setText("Monthly interest processed successfully.");
        }
    }

    @FXML private void goBack() throws IOException {
        ViewNavigator.show("DashBoardView.fxml");
    }
}
