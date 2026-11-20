import java.time.LocalDate;

public class SavingsAccount extends Account implements InterestBearing{

    //0.0005 represents 0.05%
    private final double rate = 0.0005;

    public SavingsAccount(double initialDeposit, String branch) {

        super(initialDeposit, branch);
    }

    //Restores an existing Savings Account.
    public SavingsAccount(String accountNumber, double balance, String branch, LocalDate openDate) {
        super(accountNumber, balance, branch, openDate);
    }

    //Savings Account do not allow withdrawals.
    @Override
    public Transaction withdraw(double amount) {
        throw new AccountRuleException("Withdrawals are not allowed from a Savings Account.");
    }

    @Override
    public double calculateInterest() {
        return getBalance() * rate;
    }

    @Override
    public Transaction  payInterest() {
        return addInterest(calculateInterest());
    }

    @Override
    public double getInterestRate() {
        return rate;
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}
