public class InvestmentAccount extends Account implements InterestBearing{

    //0.05 represents 5%
    private final double rate = 0.05;

    private static final double MINIMUM_INITIAL_DEPOSIT = 500.00;

    public InvestmentAccount(double initialDeposit, String branch) {
        super(checkInitialDeposit(initialDeposit), branch);
    }

    private static double checkInitialDeposit(double amount) {
        if (amount < MINIMUM_INITIAL_DEPOSIT) {
            throw new AccountRuleException("Investment Account require at least BWP500.");
        }
        return amount;
    }

    @Override
    public double calculateInterest() {
        return getBalance() * rate;
    }

    @Override
    public Transaction payInterest() {
        return addInterest(calculateInterest());
    }

    @Override
    public double getInterestRate() {
        return rate;
    }

    @Override
    public String getAccountType() {
        return "Investment";
    }


}
