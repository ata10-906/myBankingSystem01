import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // TEST 1: SAVE TRANSACTION
    public boolean saveTransaction(Transaction transaction, String accountNumber) {

        String sql = """
            INSERT INTO transactions (
                account_number,
                transaction_type,
                amount,
                transaction_date,
                new_balance
            )
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, accountNumber);
            statement.setString(2, transaction.getTransactionType().name());
            statement.setDouble(3, transaction.getAmount());
            statement.setString(4, transaction.getDate().toString());
            statement.setDouble(5, transaction.getNewBalance());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {

                        long generatedID = generatedKeys.getLong(1);
                        transaction.setTransactionID(generatedID);
                    }
                }

                System.out.println("Transaction saved successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Failed to save transaction: " + e.getMessage());
        }

        return false;
    }


    // TEST 2: GET TRANSACTIONS BY ACCOUNT
    public List<Transaction> getTransactionsByAccount(String accountNumber) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM transactions
            WHERE account_number = ?
            ORDER BY transaction_date ASC
            """;

        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Transaction transaction = extractTransaction(resultSet);
                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve transactions: " + e.getMessage());
        }

        return transactions;
    }


    // TEST 3: GET TRANSACTION BY ID
    public Transaction getTransactionByID(long transactionID) {

        String sql = """
            SELECT *
            FROM transactions
            WHERE transaction_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, transactionID);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return extractTransaction(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve transaction: " + e.getMessage());
        }

        return null;
    }



    // TEST 4: CHECK TRANSACTION EXISTS
    public boolean transactionExists(long transactionID) {

        String sql = """
            SELECT 1
            FROM transactions
            WHERE transaction_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();

             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, transactionID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Failed to check transaction: " + e.getMessage());
        }

        return false;
    }


    // TEST 5: GET ALL TRANSACTIONS
    public List<Transaction> getAllTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM transactions
            ORDER BY transaction_date ASC
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Transaction transaction = extractTransaction(resultSet);
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve all transactions: " + e.getMessage());
        }

        return transactions;
    }


    private Transaction extractTransaction(ResultSet resultSet) throws SQLException {

        long transactionID = resultSet.getLong("transaction_id");
        double amount = resultSet.getDouble("amount");
        TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transaction_type"));
        LocalDateTime date = LocalDateTime.parse(resultSet.getString("transaction_date"));
        double newBalance = resultSet.getDouble("new_balance");

        return new Transaction(transactionID, amount, transactionType, date, newBalance);
    }
}
