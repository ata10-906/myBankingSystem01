import java.util.List;

public class TransactionDAOTest {

    public static void main(String[] args) {

        // Initialise database
        DatabaseSetup.initializeDatabase();

        // Create DAO objects
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        System.out.println("\n===== TRANSACTION DAO TESTING =====");


        // TEST 1: SAVE CUSTOMER
        System.out.println("\n===== TEST 1: SAVE CUSTOMER =====");

        Customer customer = new Customer("John", "Doe", "Gaborone", 71234567L);
        boolean customerSaved = customerDAO.saveCustomer(customer);

        System.out.println("Customer saved: " + customerSaved);

        if (!customerSaved) {
            System.out.println("Testing stopped because customer could not be saved.");
            return;
        }

        String customerID = customer.getCustomerID();
        System.out.println("Customer ID: " + customerID);


        // TEST 2: SAVE ACCOUNT
        System.out.println("\n===== TEST 2: SAVE ACCOUNT =====");

        SavingsAccount account = new SavingsAccount(1000.00, "Gaborone");
        boolean accountSaved = accountDAO.saveAccount(customerID, account);

        System.out.println("Account saved: " + accountSaved);
        System.out.println("Account number: " + account.getAccountNumber());

        if (!accountSaved) {
            System.out.println("Testing stopped because account could not be saved.");
            return;
        }

        String accountNumber = account.getAccountNumber();

        accountDAO.updateAccountBalance(accountNumber, 1500.00);


        // TEST 3: SAVE TRANSACTION
        System.out.println("\n===== TEST 3: SAVE TRANSACTION =====");

        Transaction transaction = new Transaction(500.00, TransactionType.DEPOSIT, 1500.00);
        boolean transactionSaved = transactionDAO.saveTransaction(transaction, accountNumber);

        System.out.println("Transaction saved: " + transactionSaved);
        System.out.println("Transaction ID: " + transaction.getTransactionID());
        System.out.println("Transaction amount: BWP " + transaction.getAmount());
        System.out.println("Transaction type: " + transaction.getTransactionType());
        System.out.println("New balance: BWP " + transaction.getNewBalance());


        // TEST 4: RETRIEVE TRANSACTIONS BY ACCOUNT
        System.out.println("\n===== TEST 4: ACCOUNT TRANSACTION HISTORY =====");

        List<Transaction> transactions = transactionDAO.getTransactionsByAccount(accountNumber);

        System.out.println("Total transactions found: " + transactions.size());

        for (Transaction t : transactions) {

            System.out.println("-------------------------");

            System.out.println("Transaction ID: " + t.getTransactionID());
            System.out.println("Type: " + t.getTransactionType());
            System.out.println("Amount: BWP " + t.getAmount());
            System.out.println("Date: " + t.getDate());
            System.out.println("New balance: BWP " + t.getNewBalance());
        }


        // TEST 5: RETRIEVE TRANSACTION BY ID
        System.out.println("\n===== TEST 5: RETRIEVE TRANSACTION BY ID =====");

        Transaction retrievedTransaction = transactionDAO.getTransactionByID(transaction.getTransactionID());

        if (retrievedTransaction != null) {

            System.out.println("Transaction retrieved successfully!");

            System.out.println("Transaction ID: " + retrievedTransaction.getTransactionID());
            System.out.println("Transaction type: " + retrievedTransaction.getTransactionType());
            System.out.println("Amount: BWP " + retrievedTransaction.getAmount());
            System.out.println("Date: " + retrievedTransaction.getDate());
            System.out.println("New balance: BWP " + retrievedTransaction.getNewBalance());

        } else {
            System.out.println("Transaction retrieval failed.");
        }


        // TEST 6: CHECK TRANSACTION EXISTS
        System.out.println("\n===== TEST 6: TRANSACTION EXISTS =====");

        boolean exists = transactionDAO.transactionExists(transaction.getTransactionID());

        System.out.println("Transaction exists: " + exists);


        // TEST 7: VERIFY TRANSACTION DETAILS
        System.out.println("\n===== TEST 7: VERIFY TRANSACTION DETAILS =====");

        if (retrievedTransaction != null) {

            System.out.println("Amount correct: " + (retrievedTransaction.getAmount() == 500.00));
            System.out.println("Type correct: " + (retrievedTransaction.getTransactionType() == TransactionType.DEPOSIT));
            System.out.println("Balance correct: " + (retrievedTransaction.getNewBalance() == 1500.00));
        }


        // TESTING COMPLETED
        System.out.println("\n===== TRANSACTION DAO TESTING COMPLETED =====");
    }
}
