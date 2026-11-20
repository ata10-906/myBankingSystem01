
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

    public static void initializeDatabase() {

        // 1. Customers table
        String createCustomersTable = """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id TEXT PRIMARY KEY,
                first_name TEXT NOT NULL,
                surname TEXT NOT NULL,
                address TEXT NOT NULL,
                contact_no INTEGER NOT NULL
            );
            """;

        // 2. Credentials table
        String createCredentialsTable = """
            CREATE TABLE IF NOT EXISTS credentials (
                customer_id TEXT PRIMARY KEY,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,

                FOREIGN KEY (customer_id)
                    REFERENCES customers(customer_id)
                    ON DELETE RESTRICT
            );
            """;

        // 3. Accounts table
        String createAccountsTable = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_number TEXT PRIMARY KEY,
                customer_id TEXT NOT NULL,
                account_type TEXT NOT NULL,
                balance REAL NOT NULL,
                branch TEXT NOT NULL,
                open_date TEXT NOT NULL,

                employer_company TEXT,
                employer_address TEXT,
                employee_number INTEGER,

                FOREIGN KEY (customer_id)
                    REFERENCES customers(customer_id)
                    ON DELETE RESTRICT,

                CHECK (
                    account_type IN (
                        'Savings',
                        'Investment',
                        'Cheque'
                    )
                )
            );
            """;

        // 4. Transactions table
        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_number TEXT NOT NULL,
                transaction_type TEXT NOT NULL,
                amount REAL NOT NULL,
                transaction_date TEXT NOT NULL,
                new_balance REAL NOT NULL,

                FOREIGN KEY (account_number)
                    REFERENCES accounts(account_number)
                    ON DELETE RESTRICT,

                CHECK (amount > 0)
            );
            """;

        // 5. Index for faster account lookups
        String createAccountIndex = """
            CREATE INDEX IF NOT EXISTS idx_accounts_customer
            ON accounts(customer_id);
            """;

        // 6. Index for faster transaction history lookups
        String createTransactionIndex = """
            CREATE INDEX IF NOT EXISTS idx_transactions_account
            ON transactions(account_number);
            """;

        // Establish database connection
        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            // Enable foreign key enforcement
            try (Statement statement =
                         connection.createStatement()) {

                statement.execute("PRAGMA foreign_keys = ON");

                // Create tables
                statement.execute(createCustomersTable);
                statement.execute(createCredentialsTable);
                statement.execute(createAccountsTable);
                statement.execute(createTransactionsTable);

                // Create indexes
                statement.execute(createAccountIndex);
                statement.execute(createTransactionIndex);
            }

            System.out.println(
                    "Database tables created successfully!"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database setup failed: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
