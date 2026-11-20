import java.util.*;
import java.util.UUID;

public class Customer {

    private String customerID;
    private String firstname;
    private String surname;
    private String address;
    private long contactNo;

    private List<Account> accounts;
    private Credentials credentials;

    public Customer(String firstname, String surname, String address, long contactNo) {

        customerID = "C-" + UUID.randomUUID();

        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.contactNo = contactNo;

        accounts = new ArrayList<>();
    }

    // Constructor for restoring an existing customer
    public Customer(String customerID, String firstname, String surname, String address, long contactNo) {

        this.customerID = customerID;
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.contactNo = contactNo;

        this.accounts = new ArrayList<>();
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

    // Removes an account if database saving fails.
    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    // Adds an existing account retrieved from the database.
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }

    //Return an unmodifiable list of customer accounts.
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

    //Assign credentials to the customer
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    //Returns customer credentials
    public Credentials getCredentials() {
        return credentials;
    }

    //Return customer ID
    public String getCustomerID() {
        return customerID;
    }

    //Return first name
    public String getFirstName() {
        return firstname;
    }

    //Return surname
    public String getSurname() {
        return surname;
    }

    //Return address
    public String getAddress() {
        return address;
    }

    //Return contact number
    public long getContactNo() {
        return contactNo;
    }
}
