public class BankingSystemDemo {

    public static void main (String[] args) {
        try {
            //1.Create a customer.
            Customer customer = new Customer("Motheo", "Tselakgosi", "P.O.Box 40453 Gaborone", 71423434L);

            //2.Open a Savings Account.
            Account savings = customer.openAccount("savings", 1000.00, "Main Branch", null);

            //3.Deposit money,
            savings.deposit(200.00);

            //4.Process interest.
            SavingsAccount savingsAccount = (SavingsAccount) savings;
            savingsAccount.payInterest();

            //5.Display account details.
            System.out.println("Customer ID: " + customer.getCustomerID());
            System.out.println("Account Type: " + savings.getAccountType());
            System.out.printf("Balance: BWP %.2f%n", savings.getBalance());

            //6.Display transaction history.
            System.out.println("\nTransaction History:");
            for (Transaction transaction : savings.getTransactions()) {
                System.out.println(transaction);
            }

            //7.Open an Investment Account.
           Account investment = customer.openAccount("investment", 500.00, "Main Branch", null);

            //8.Create employer information.
            Employer employer = new Employer("Edgars Company", "007 Gaborone", 12308L);

            //9.Open a Cheque Account.
            Account cheque = customer.openAccount("cheque", 0.00, "Main Branch", employer);

            //10.Display the number of customer accounts.
            System.out.println("\nTotal accounts: " + customer.getAccounts().size());

        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
