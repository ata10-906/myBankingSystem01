public class ChequeAccount extends Account{

    private Employer employer;

    public ChequeAccount(double initialDeposit, String branch, Employer employer) {
        super(initialDeposit, branch);

        if (employer == null) {
            throw new AccountRuleException("Cheque Account requires employer information.");
        }
        this.employer = employer;
    }

    public Employer getEmployer() {
        return employer;
    }

    @Override
    public String getAccountType() {
        return "Cheque";
    }
}
