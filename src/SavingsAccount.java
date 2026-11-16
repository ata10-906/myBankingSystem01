public class SavingsAccount extends Account implements InterestBearing{

    //0.0005 represents 0.05%
    private final double rate = 0.0005;

    public SavingsAccount(double initialDeposit, String branch) {

        super(initialDeposit, branch);
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
