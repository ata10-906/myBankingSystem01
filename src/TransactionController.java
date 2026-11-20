import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;


public class TransactionController {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

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

            // Connect to the database through AccountDAO.
            AccountDAO accountDAO = new AccountDAO();

            // Process the deposit in the database.
            Transaction transaction = accountDAO.processDeposit(account.getAccountNumber(), amount);

            if (transaction == null) {
                messageLabel.setText("Deposit failed. Please try again.");
                return;
            }

            // Update the account currently held in memory.
            account.deposit(amount);

            // Display the successful deposit.
            messageLabel.setText(String.format("Deposit successful. Balance: BWP %.2f", account.getBalance()));

        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());

        } catch (RuntimeException e) {
            messageLabel.setText("An error occurred while processing the deposit.");
            System.out.println("Deposit error: " + e.getMessage());
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

            // Process the withdrawal in the database.
            Transaction transaction = accountDAO.processWithdrawal(account.getAccountNumber(), amount);

            // Check whether the withdrawal was successful.
            if (transaction == null) {
                messageLabel.setText("Withdrawal failed. Insufficient funds or database error.");
                return;
            }

            // Update the account currently held in memory.
            account.withdraw(amount);

            // Display the successful withdrawal.
            messageLabel.setText(String.format("Withdrawal successful. Balance: BWP %.2f", account.getBalance()));

        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());

        } catch (RuntimeException e) {
            messageLabel.setText("An error occurred while processing the withdrawal.");
            System.out.println("Withdrawal error: " + e.getMessage());
        }
    }

    @FXML private void handleHistory() {

        Account account = AppSession.getSelectedAccount();

        if (account == null) {
            messageLabel.setText("Select an account first.");
            return;
        }

        historyArea.clear();

        // Retrieve transactions from the database.
        List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());

        // Check whether the account has any transactions.
        if (transactions.isEmpty()) {
            historyArea.setText("No transactions found for this account.");
            return;
        }

        // Display the retrieved transactions.
        for (Transaction transaction : transactions) {
            historyArea.appendText(transaction + "\n");
        }

        messageLabel.setText("Transaction history loaded successfully.");
    }


    @FXML
    private void goBack() throws IOException {
        ViewNavigator.show("DashBoardView.fxml");
    }
}
