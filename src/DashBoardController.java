import javafx.fxml.FXML;

public class DashBoardController {

    @FXML
    private void openCustomer() throws Exception {
        ViewNavigator.show("CustomerView.fxml");
    }

    @FXML
    private void openAccounts() throws Exception {
        ViewNavigator.show("AccountView.fxml");
    }

    @FXML
    private void openTransactions() throws Exception {
        ViewNavigator.show("TransactionView.fxml");
    }

    @FXML
    private void openInterest() throws Exception {
        ViewNavigator.show("InterestView.fxml");
    }

    @FXML
    private void handleLogout() throws Exception {

        //Clear the current customer's session.
        AppSession.logout();

        //Return to the login screen.
        ViewNavigator.show("LoginView.fxml");
    }
}
