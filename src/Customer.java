import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {

    private static long nextCustomerID = 1;

    private String customerID;
    private String firstname;
    private String surname;
    private String address;
    private long contactNo;

    private List<Account> accounts;
    private Credentials credentials;

    public Customer(String firstname, String surname, String address, long contactNo) {
        customerID = "C" + nextCustomerID++;

        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.contactNo = contactNo;

        accounts = new ArrayList<>();
    }

    //Opens an account based on the selected account type.
    public Account openAccount(String accountType, double initialDeposit, String branch, Employer employer) {
        Account account;

        switch (accountType.toLowerCase()) {
            case "savings":
                account = new SavingsAccount(initialDeposit, branch);
                break;

            case  "investment":
                account = new InvestmentAccount(initialDeposit, branch);
                break;

            case "cheque":
                account = new ChequeAccount(initialDeposit, branch, employer);
                break;

            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }

        accounts.add(account);
        return account;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    //Update customer information.
    public void updateDetails(String firstname, String surname, String address, long contactNo) {
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.contactNo = contactNo;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public long getcontactNo() {
        return contactNo;
    }
}
