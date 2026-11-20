
import java.util.List;

public class AccountDAOTest {

    public static void main(String[] args) {

        // Initialise the database and create DAO objects.
        DatabaseSetup.initializeDatabase();

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();

        System.out.println("===== ACCOUNT DAO TESTING =====");


        // TEST 1: CREATE AND SAVE A CUSTOMER
        System.out.println("\n===== TEST 1: SAVE CUSTOMER =====");

        Customer customer = new Customer("Tom", "Holland", "Gaborone", 71234567L
        );

        boolean customerSaved = customerDAO.saveCustomer(customer);
        System.out.println("Customer saved: " + customerSaved);

        if (!customerSaved) {
            System.out.println("Testing stopped: customer could not be saved.");
            return;
        }

        String customerID = customer.getCustomerID();
        System.out.println("Customer ID: " + customerID);


        // TEST 2: SAVE A SAVINGS ACCOUNT
        System.out.println("\n===== TEST 2: SAVE SAVINGS ACCOUNT =====");

        SavingsAccount savingsAccount = new SavingsAccount(1000.00, "Gaborone");
        boolean savingsSaved = accountDAO.saveAccount(customerID, savingsAccount);

        System.out.println("Savings account saved: " + savingsSaved);
        System.out.println("Account number: " + savingsAccount.getAccountNumber());


        // TEST 3: SAVE AN INVESTMENT ACCOUNT
        System.out.println("\n===== TEST 3: SAVE INVESTMENT ACCOUNT =====");

        InvestmentAccount investmentAccount = new InvestmentAccount(500.00, "Francistown");
        boolean investmentSaved = accountDAO.saveAccount(customerID, investmentAccount);

        System.out.println("Investment account saved: " + investmentSaved);
        System.out.println("Account number: " + investmentAccount.getAccountNumber());


        // TEST 4: SAVE CHEQUE ACCOUNT
        System.out.println("\n===== TEST 4: SAVE CHEQUE ACCOUNT =====");

        // Create Employer object
        Employer employer = new Employer("ABC Company", "Gaborone", 12345L);

        ChequeAccount chequeAccount = new ChequeAccount(2000.00, "Gaborone", employer);
        boolean chequeSaved = accountDAO.saveAccount(customerID, chequeAccount);

        System.out.println("Cheque account saved: " + chequeSaved);
        System.out.println("Account number: " + chequeAccount.getAccountNumber());


        // TEST 5: RETRIEVE CHEQUE ACCOUNT
        System.out.println("\n===== TEST 5: RETRIEVE CHEQUE ACCOUNT =====");

        Account retrievedChequeAccount = accountDAO.getAccountByNumber(chequeAccount.getAccountNumber());

        if (retrievedChequeAccount != null) {

            System.out.println("Cheque account retrieved successfully!");
            System.out.println("Account type: " + retrievedChequeAccount.getAccountType());
            System.out.println("Account number: " + retrievedChequeAccount.getAccountNumber());
            System.out.println("Balance: " + retrievedChequeAccount.getBalance());
            System.out.println("Branch: " + retrievedChequeAccount.getBranch());

            if (retrievedChequeAccount instanceof ChequeAccount cheque) {

                Employer retrievedEmployer = cheque.getEmployer();

                System.out.println("\n--- EMPLOYER DETAILS ---");
                System.out.println("Company name: " + retrievedEmployer.getCompanyName());
                System.out.println("Company address: " + retrievedEmployer.getCompanyAddress());
                System.out.println("Employee number: " + retrievedEmployer.getEmployeeNumber());

            } else {
                System.out.println("Error: Account was not reconstructed as a Cheque Account.");
            }

        } else {
            System.out.println("Cheque account retrieval failed.");
        }


        // TEST 6: RETRIEVE AN ACCOUNT
        System.out.println("\n===== TEST 6: RETRIEVE ACCOUNT =====");

        Account retrievedAccount = accountDAO.getAccountByNumber(savingsAccount.getAccountNumber());

        if (retrievedAccount != null) {

            System.out.println("Account retrieved successfully!");
            System.out.println("Account number: " + retrievedAccount.getAccountNumber());
            System.out.println("Account type: " + retrievedAccount.getAccountType());
            System.out.println("Balance: " + retrievedAccount.getBalance());
            System.out.println("Branch: " + retrievedAccount.getBranch());

        } else {
            System.out.println("Account retrieval failed.");
        }


        // TEST 7: CHECK WHETHER AN ACCOUNT EXISTS
        System.out.println("\n===== TEST 7: ACCOUNT EXISTS =====");

        boolean exists = accountDAO.accountExists(
                savingsAccount.getAccountNumber()
        );

        System.out.println("Account exists: " + exists);


        // TEST 8: UPDATE ACCOUNT BALANCE
        System.out.println("\n===== TEST 8: UPDATE ACCOUNT BALANCE =====");

        boolean updated = accountDAO.updateAccountBalance(savingsAccount.getAccountNumber(), 1500.00);
        System.out.println("Account balance updated: " + updated);


        // TEST 9: VERIFY UPDATED BALANCE
        System.out.println("\n===== TEST 9: VERIFY UPDATED BALANCE =====");

        Account updatedAccount = accountDAO.getAccountByNumber(savingsAccount.getAccountNumber());

        if (updatedAccount != null) {

            System.out.println("Updated balance: " + updatedAccount.getBalance());

        } else {
            System.out.println("Could not retrieve updated account.");
        }


        // TEST 10: RETRIEVE ALL CUSTOMER ACCOUNTS
        System.out.println("\n===== TEST 10: CUSTOMER ACCOUNTS =====");

        List<Account> customerAccounts = accountDAO.getAccountsByCustomerID(customerID);
        System.out.println("Total accounts found: " + customerAccounts.size());

        for (Account account : customerAccounts) {

            System.out.println("-------------------------");
            System.out.println("Account number: " + account.getAccountNumber());
            System.out.println("Account type: " + account.getAccountType());
            System.out.println("Balance: " + account.getBalance());
            System.out.println("Branch: " + account.getBranch());
        }


        System.out.println("\n===== ACCOUNT DAO TESTING COMPLETED =====");
    }
}

