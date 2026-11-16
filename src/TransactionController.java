import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class TransactionController {

    @FXML private TextField amountField;
    @FXML private Label messageLabel;
    @FXML private TextArea historyArea;

    private double readAmount() {
        double amount = Double.parseDouble(amountField.getText().trim());

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount;
    }

    @FXML private void handleDeposit() {
        Account account = AppSession.getSelectedAccount();

        if (account == null) {
            messageLabel.setText("Select an account first.");
            return;
        }
        try {
            double amount = readAmount();
            account.deposit(amount);
            messageLabel.setText(String.format("Deposit successful. Balance: BWP %.2f", account.getBalance()));
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML private void handleWithdraw() {
        Account account = AppSession.getSelectedAccount();

        if (account == null) {
            messageLabel.setText("Select an account first.");
            return;
        }

        try {
            double amount = readAmount();
            account.withdraw(amount);
            messageLabel.setText(String.format("Withdrawal successful. Balance: BWP %.2f", account.getBalance()));
        } catch (RuntimeException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML private void handleHistory() {
        Account account = AppSession.getSelectedAccount();

        if (account == null) {
            messageLabel.setText("Select an account first.");
            return;
        }

        historyArea.clear();

        for (Transaction transaction : account.getTransactions()) {
            historyArea.appendText(transaction + "\n");
        }
    }

    @FXML private void goBack() throws Exception {
        ViewNavigator.show("DashBoardView.fxml");
    }
}
