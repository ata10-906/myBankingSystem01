import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {

    private static long nextAccountNumber = 1000001;

    private String accountNumber;
    protected double balance;
    private String branch;
    private LocalDate openDate;
    private List<Transaction> transactions;

    public Account(double initialDeposit, String branch) {

        if (initialDeposit < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative.");
        }

        accountNumber = String.valueOf(nextAccountNumber++);
        balance = 0.0;
        this.branch = branch;
        openDate = LocalDate.now();

        transactions = new ArrayList<>();

        if (initialDeposit > 0) {
            deposit(initialDeposit);
        }
    }

    //Restore an existing account from the database.
    protected Account(String accountNumber, double balance, String branch, LocalDate openDate) {

        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.openDate = openDate;

        transactions = new ArrayList<>();

        //Prevent future account numbers from colliding.
        try {
            long restoredNumber = Long.parseLong(accountNumber);

            if (restoredNumber >= nextAccountNumber) {
                nextAccountNumber = restoredNumber + 1;
            }
        } catch (NumberFormatException e) {
            //Account number is not numeric.
        }
    }



    //Deposits money into the account.
    public Transaction deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit must be greater than zero.");
        }
        balance += amount;

        return recordTransaction(amount, TransactionType.DEPOSIT);
    }

    //Withdraws money from the account.
    public Transaction withdraw(double amount) {
        checkPositiveAmount(amount);

        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds.");
        }
        balance -= amount;

        return recordTransaction(amount, TransactionType.WITHDRAWAL);
    }

    protected void checkPositiveAmount(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    //Creates and stores a transaction.
    protected Transaction recordTransaction(double amount, TransactionType transactionType) {

        Transaction transaction = new Transaction(amount, transactionType, balance);
        transactions.add(transaction);
        return transaction;
    }

    //Adds interest and records the transactions.
    protected Transaction addInterest(double interest) {
        if (interest <= 0) {
            throw new AccountRuleException("Interest must be greater than zero");
        }
        balance += interest;
        return recordTransaction(interest, TransactionType.INTEREST);
    }

    // Updates the next account number based on existing database records.
    public static void setNextAccountNumber(long nextNumber) {
        nextAccountNumber = nextNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    //Each account subclass must provide its own account type.
    public abstract String getAccountType();

}
