import java.time.LocalDate;

public class ChequeAccount extends Account{

    private Employer employer;

    public ChequeAccount(double initialDeposit, String branch, Employer employer) {

        super(initialDeposit, branch);

        if (employer == null) {
            throw new AccountRuleException("Cheque Account requires employer information.");
        }
        this.employer = employer;
    }

    //Restores an existing Cheque Account.
    public ChequeAccount(String accountNumber, double balance, String branch, LocalDate openDate, Employer employer) {

        super(accountNumber, balance, branch, openDate);

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
