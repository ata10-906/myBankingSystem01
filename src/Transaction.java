import java.time.LocalDateTime;

public class Transaction {

    private static long nextID = 1;

    private long transactionID;
    private double amount;
    private TransactionType transactionType;
    private LocalDateTime date;
    private double newBalance;

    public Transaction(double amount, TransactionType type, double newBalance) {

        transactionID = nextID++;
        this.amount = amount;
        this.transactionType = type;
        this.date = LocalDateTime.now();
        this.newBalance = newBalance;
    }


    // Restores an existing transaction from the database.
    public Transaction(long transactionID, double amount, TransactionType type, LocalDateTime date, double newBalance) {

        this.transactionID = transactionID;
        this.amount = amount;
        this.transactionType = type;
        this.date = date;
        this.newBalance = newBalance;

        if (transactionID >= nextID) {
            nextID = transactionID + 1;
        }
    }

    // Allows the database-generated ID to be assigned to this transaction.
    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;

        if (transactionID >= nextID) {
            nextID = transactionID + 1;
        }
    }

    public double getAmount() {
        return amount;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getNewBalance() {
        return newBalance;
    }

    @Override
    public String toString() {
        return transactionID + " | " + transactionType + " | Amount: BWP " + String.format("%.2f", amount) + " | Balance: BWP " + String.format("%.2f", newBalance);
    }
}
