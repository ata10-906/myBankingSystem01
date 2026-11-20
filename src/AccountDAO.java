import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Synchronizes account-number generation with the database.
    public AccountDAO() {
        synchronizeAccountNumber();
    }

    // Finds the highest account number stored in the database.
    private void synchronizeAccountNumber() {

        String sql = """
        SELECT COALESCE(MAX(CAST(account_number AS INTEGER)), 1000000)
        AS highest_account_number
        FROM accounts
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {

                long highestAccountNumber = resultSet.getLong("highest_account_number");
                Account.setNextAccountNumber(highestAccountNumber + 1);

                System.out.println("Next account number: " + (highestAccountNumber + 1));
            }

        } catch (SQLException e) {

            System.out.println("Failed to synchronize account numbers: " + e.getMessage());
        }
    }

    // Saves a new account into the database.
    public boolean saveAccount(String customerID, Account account) {

        String sql = """
            INSERT INTO accounts (
                account_number,
                customer_id,
                account_type,
                balance,
                branch,
                open_date,
                employer_company,
                employer_address,
                employee_number
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, account.getAccountNumber());
            statement.setString(2, customerID);
            statement.setString(3, account.getAccountType());
            statement.setDouble(4, account.getBalance());
            statement.setString(5, account.getBranch());
            statement.setString(6, account.getOpenDate().toString());

            if (account instanceof ChequeAccount chequeAccount) {

                Employer employer = chequeAccount.getEmployer();

                statement.setString(7, employer.getCompanyName());
                statement.setString(8, employer.getCompanyAddress());
                statement.setLong(9, employer.getEmployeeNumber());

            } else {
                statement.setNull(7, java.sql.Types.VARCHAR);
                statement.setNull(8, java.sql.Types.VARCHAR);
                statement.setNull(9, java.sql.Types.INTEGER);
            }

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to save account: " + e.getMessage());

            return false;
        }
    }


    // Retrieves an account using its account number.
    public Account getAccountByNumber(String accountNumber) {

        String sql = """
            SELECT *
            FROM accounts
            WHERE account_number = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return createAccountFromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve account: " + e.getMessage());
        }

        return null;
    }


    // Retrieves all accounts belonging to a customer.
    public List<Account> getAccountsByCustomerID(String customerID) {

        List<Account> accounts = new ArrayList<>();

        String sql = """
            SELECT *
            FROM accounts
            WHERE customer_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerID);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Account account = createAccountFromResultSet(resultSet);
                    accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve customer accounts: " + e.getMessage());
        }

        return accounts;
    }


    // Updates the balance of an existing account.
    public boolean updateAccountBalance(String accountNumber, double newBalance) {

        String sql = """
            UPDATE accounts
            SET balance = ?
            WHERE account_number = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, newBalance);
            statement.setString(2, accountNumber);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to update account balance: " + e.getMessage());
            return false;
        }
    }

    // Processes a deposit and saves both the balance and transaction atomically.
    public Transaction processDeposit(String accountNumber, double amount) {

        if (amount <= 0) {
            return null;
        }

        String getBalanceSQL = """
        SELECT balance
        FROM accounts
        WHERE account_number = ?
        """;

        String updateBalanceSQL = """
        UPDATE accounts
        SET balance = ?
        WHERE account_number = ?
        """;

        String insertTransactionSQL = """
        INSERT INTO transactions (
            account_number,
            transaction_type,
            amount,
            transaction_date,
            new_balance
        )
        VALUES (?, ?, ?, ?, ?)
        """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();

            // Begin the database transaction.
            connection.setAutoCommit(false);

            double currentBalance;

            // Step 1: Retrieve the current balance.
            try (PreparedStatement statement = connection.prepareStatement(getBalanceSQL)) {

                statement.setString(1, accountNumber);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return null;
                    }

                    currentBalance = resultSet.getDouble("balance");
                }
            }

            // Step 2: Calculate the new balance.
            double newBalance = currentBalance + amount;

            // Step 3: Update the account balance.
            try (PreparedStatement statement = connection.prepareStatement(updateBalanceSQL)) {

                statement.setDouble(1, newBalance);
                statement.setString(2, accountNumber);

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return null;
                }
            }

            // Step 4: Create the deposit transaction.
            Transaction transaction = new Transaction(amount, TransactionType.DEPOSIT, newBalance);

            // Step 5: Save the transaction.
            try (PreparedStatement statement = connection.prepareStatement(insertTransactionSQL, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, accountNumber);
                statement.setString(2, transaction.getTransactionType().name());
                statement.setDouble(3, transaction.getAmount());
                statement.setString(4, transaction.getDate().toString());
                statement.setDouble(5, transaction.getNewBalance());

                statement.executeUpdate();

                // Retrieve the database-generated transaction ID.
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        transaction.setTransactionID(generatedKeys.getLong(1));
                    }
                }
            }

            // Step 6: Commit both database operations.
            connection.commit();

            System.out.println("Deposit processed successfully!");

            return transaction;

        } catch (SQLException e) {

            System.out.println("Deposit failed: " + e.getMessage());

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackError) {
                System.out.println("Rollback failed: " + rollbackError.getMessage());
            }

            return null;

        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection: " + e.getMessage());
            }
        }
    }


    // Processes a withdrawal and saves both the balance and transaction atomically.
    public Transaction processWithdrawal(String accountNumber, double amount) {

        if (amount <= 0) {
            return null;
        }

        String getBalanceSQL = """
        SELECT balance
        FROM accounts
        WHERE account_number = ?
        """;

        String updateBalanceSQL = """
        UPDATE accounts
        SET balance = ?
        WHERE account_number = ?
        """;

        String insertTransactionSQL = """
        INSERT INTO transactions (
            account_number,
            transaction_type,
            amount,
            transaction_date,
            new_balance
        )
        VALUES (?, ?, ?, ?, ?)
        """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();

            // Begin the database transaction.
            connection.setAutoCommit(false);

            double currentBalance;

            // Step 1: Retrieve the current balance.
            try (PreparedStatement statement = connection.prepareStatement(getBalanceSQL)) {

                statement.setString(1, accountNumber);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return null;
                    }

                    currentBalance = resultSet.getDouble("balance");
                }
            }

            // Step 2: Check whether sufficient funds are available.
            if (amount > currentBalance) {
                connection.rollback();
                return null;
            }

            // Step 3: Calculate the new balance.
            double newBalance = currentBalance - amount;

            // Step 4: Update the account balance.
            try (PreparedStatement statement = connection.prepareStatement(updateBalanceSQL)) {

                statement.setDouble(1, newBalance);
                statement.setString(2, accountNumber);

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return null;
                }
            }

            // Step 5: Create the withdrawal transaction.
            Transaction transaction = new Transaction(amount, TransactionType.WITHDRAWAL, newBalance);

            // Step 6: Save the transaction.
            try (PreparedStatement statement = connection.prepareStatement(insertTransactionSQL, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, accountNumber);
                statement.setString(2, transaction.getTransactionType().name());
                statement.setDouble(3, transaction.getAmount());
                statement.setString(4, transaction.getDate().toString());
                statement.setDouble(5, transaction.getNewBalance());

                statement.executeUpdate();

                // Retrieve the database-generated transaction ID.
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        transaction.setTransactionID(generatedKeys.getLong(1));
                    }
                }
            }

            // Step 7: Commit both database operations.
            connection.commit();

            System.out.println("Withdrawal processed successfully!");
            return transaction;

        } catch (SQLException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackError) {
                System.out.println("Rollback failed: " + rollbackError.getMessage());
            }

            return null;

        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection: " + e.getMessage());
            }
        }
    }

    // Processes monthly interest and saves both the balance and transaction atomically.
    public Transaction processInterest(String accountNumber, double interestAmount) {

        if (interestAmount <= 0) {
            return null;
        }

        String getBalanceSQL = """
        SELECT balance
        FROM accounts
        WHERE account_number = ?
        """;

        String updateBalanceSQL = """
        UPDATE accounts
        SET balance = ?
        WHERE account_number = ?
        """;

        String insertTransactionSQL = """
        INSERT INTO transactions (
            account_number,
            transaction_type,
            amount,
            transaction_date,
            new_balance
        )
        VALUES (?, ?, ?, ?, ?)
        """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();

            // Begin database transaction.
            connection.setAutoCommit(false);

            double currentBalance;

            // Step 1: Retrieve current balance.
            try (PreparedStatement statement = connection.prepareStatement(getBalanceSQL)) {

                statement.setString(1, accountNumber);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return null;
                    }

                    currentBalance = resultSet.getDouble("balance");
                }
            }

            // Step 2: Calculate new balance after interest.
            double newBalance = currentBalance + interestAmount;

            // Step 3: Update account balance.
            try (PreparedStatement statement = connection.prepareStatement(updateBalanceSQL)) {

                statement.setDouble(1, newBalance);
                statement.setString(2, accountNumber);

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return null;
                }
            }

            // Step 4: Create interest transaction.
            Transaction transaction = new Transaction(interestAmount, TransactionType.INTEREST, newBalance);

            // Step 5: Save transaction.
            try (PreparedStatement statement = connection.prepareStatement(insertTransactionSQL, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, accountNumber);
                statement.setString(2, transaction.getTransactionType().name());
                statement.setDouble(3, transaction.getAmount());
                statement.setString(4, transaction.getDate().toString());
                statement.setDouble(5, transaction.getNewBalance());

                statement.executeUpdate();

                // Retrieve generated transaction ID.
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        transaction.setTransactionID(generatedKeys.getLong(1));
                    }
                }
            }

            // Step 6: Commit both operations.
            connection.commit();

            System.out.println("Interest processed successfully!");
            return transaction;

        } catch (SQLException e) {

            System.out.println("Interest processing failed: " + e.getMessage());

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackError) {
                System.out.println("Rollback failed: " + rollbackError.getMessage());
            }

            return null;

        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection: " + e.getMessage());
            }
        }
    }


    // Checks whether an account exists.
    public boolean accountExists(String accountNumber) {

        String sql = """
            SELECT 1
            FROM accounts
            WHERE account_number = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Failed to check account: " + e.getMessage());
            return false;
        }
    }


    // Creates the correct Account subclass from database data.
    private Account createAccountFromResultSet(ResultSet resultSet) throws SQLException {

        String accountNumber = resultSet.getString("account_number");
        double balance = resultSet.getDouble("balance");
        String branch = resultSet.getString("branch");
        LocalDate openDate = LocalDate.parse(resultSet.getString("open_date"));
        String accountType = resultSet.getString("account_type");

        switch (accountType) {

            case "Savings":
                return new SavingsAccount(accountNumber, balance, branch, openDate);


            case "Investment":
                return new InvestmentAccount(accountNumber, balance, branch, openDate);


            case "Cheque":
                Employer employer = new Employer(resultSet.getString("employer_company"), resultSet.getString("employer_address"), resultSet.getLong("employee_number"));

                return new ChequeAccount(accountNumber, balance, branch, openDate, employer);


            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }
}