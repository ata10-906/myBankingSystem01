import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class AccountController {

    @FXML private ComboBox<String> accountTypeBox;
    @FXML private TextField depositField;
    @FXML private TextField branchField;

    // Employer details are required only for Cheque Accounts.
    @FXML private VBox employerFields;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;
    @FXML private TextField employeeNumberField;

    // This ComboBox stores Account objects, not account-description Strings.
    @FXML private ComboBox<Account> existingAccountBox;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        accountTypeBox.getItems().setAll("Savings", "Investment", "Cheque");

        // Display an Account in the ComboBox using readable information.
        existingAccountBox.setConverter(new StringConverter<Account>() {

            @Override
            public String toString(Account account) {
                if (account == null) {
                    return "";
                }
                return account.getAccountType() + " - "
                        + account.getAccountNumber();
            }

            @Override
            public Account fromString(String text) {
                return null;
            }
        });

        employerFields.setVisible(false);
        employerFields.setManaged(false);

        refreshAccounts();
    }

    // Show employer fields immediately when Cheque is selected.
    @FXML
    private void handleAccountTypeChanged() {

        boolean isCheque = "Cheque".equals(accountTypeBox.getValue());

        employerFields.setVisible(isCheque);
        employerFields.setManaged(isCheque);

        if (isCheque) {
            messageLabel.setText(
                    "Enter all employer details to open a Cheque Account."
            );
        } else {
            messageLabel.setText("");
        }
    }

    // Create a new account.
    @FXML
    private void handleOpenAccount() {

        try {
            String type = accountTypeBox.getValue();

            if (type == null) {
                messageLabel.setText("Please select an account type.");
                return;
            }

            String branch = branchField.getText().trim();
            String depositText = depositField.getText().trim();

            if (branch.isEmpty() || depositText.isEmpty()) {
                messageLabel.setText(
                        "Please enter the branch and initial deposit."
                );
                return;
            }

            double amount = Double.parseDouble(depositText);

            if (!Double.isFinite(amount) || amount <= 0) {
                messageLabel.setText(
                        "Enter a valid initial deposit greater than zero."
                );
                return;
            }

            Customer customer = AppSession.getCurrentCustomer();

            if (customer == null) {
                messageLabel.setText("Please log in first.");
                return;
            }

            // The InvestmentAccount model also checks this minimum.
            if ("Investment".equals(type) && amount < 500.00) {
                messageLabel.setText(
                        "An Investment Account requires at least BWP 500.00."
                );
                return;
            }

            Employer employer = null;

            if ("Cheque".equals(type)) {
                String companyName = companyNameField.getText().trim();
                String companyAddress = companyAddressField.getText().trim();
                String employeeText = employeeNumberField.getText().trim();

                if (companyName.isEmpty()
                        || companyAddress.isEmpty()
                        || employeeText.isEmpty()) {
                    messageLabel.setText(
                            "Please enter all employer details."
                    );
                    return;
                }

                long employeeNumber = Long.parseLong(employeeText);

                if (employeeNumber <= 0) {
                    messageLabel.setText(
                            "Employee number must be greater than zero."
                    );
                    return;
                }

                employer = new Employer(
                        companyName,
                        companyAddress,
                        employeeNumber
                );
            }

            // Customer.openAccount creates the correct subclass:
            // SavingsAccount, InvestmentAccount, or ChequeAccount.
            Account account = customer.openAccount(type, amount, branch, employer);

            // Keep the newly created account in the active application session.
            AppSession.setSelectedAccount(account);

            // Reload the logged-in customer's accounts in the ComboBox.
            refreshAccounts();
            existingAccountBox.setValue(account);

            messageLabel.setText(
                    type + " account opened successfully. Account number: " + account.getAccountNumber());

            // Clear the form after a successful account opening.
            depositField.clear();
            branchField.clear();
            companyNameField.clear();
            companyAddressField.clear();
            employeeNumberField.clear();

            accountTypeBox.setValue(null);
            handleAccountTypeChanged();

        } catch (NumberFormatException e) {
            messageLabel.setText(
                    "Enter a valid number for the deposit and employee number."
            );
        } catch (RuntimeException e) {
            messageLabel.setText(
                    "Account could not be opened: " + e.getMessage()
            );
        }
    }

    // Load accounts belonging to the currently logged-in Customer.
    private void refreshAccounts() {
        existingAccountBox.getItems().clear();

        Customer customer = AppSession.getCurrentCustomer();

        if (customer == null) {
            AppSession.setSelectedAccount(null);
            return;
        }

        // Customer.getAccounts() supplies the actual Account objects.
        existingAccountBox.getItems().setAll(customer.getAccounts());

        Account selected = AppSession.getSelectedAccount();

        // Keep the selected account only if it belongs to this customer.
        if (selected != null && customer.getAccounts().contains(selected)) {
            existingAccountBox.setValue(selected);
        } else {
            AppSession.setSelectedAccount(null);
            existingAccountBox.getSelectionModel().clearSelection();
        }
    }

    // Save the account selected in the ComboBox to AppSession.
    @FXML
    private void handleSelectAccount() {
        Account selectedAccount = existingAccountBox.getValue();

        if (selectedAccount == null) {
            AppSession.setSelectedAccount(null);
            messageLabel.setText("Please select an account.");
            return;
        }

        //Stores the selected account.
        AppSession.setSelectedAccount(selectedAccount);
        messageLabel.setText("Selected " + selectedAccount.getAccountType() + " account: " + selectedAccount.getAccountNumber());
    }

    // Display the balance of the selected account.
    @FXML
    private void handleCheckBalance() {
        Account account = existingAccountBox.getValue();

        if (account == null) {
            account = AppSession.getSelectedAccount();
        }

        if (account == null) {
            messageLabel.setText("Please select an account first.");
            return;
        }

        AppSession.setSelectedAccount(account);
        messageLabel.setText(
                String.format("Current Balance: BWP %.2f", account.getBalance())
        );
    }

    // Return to the customer dashboard.
    @FXML
    private void goBack() throws Exception {
        ViewNavigator.show("DashBoardView.fxml");
    }
}

