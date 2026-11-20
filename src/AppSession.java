import java.util.ArrayList;
import java.util.List;

public class AppSession {

    // Stores customers currently available during the application session.
    private final static List<Customer> customers = new ArrayList<>();

    // Stores the currently logged-in customer.
    private static Customer currentCustomer;

    // Stores the account currently selected by the customer.
    private static Account selectedAccount;


    // Adds a customer to the current application session.
    public static void addCustomer(Customer customer) {

        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
        }
    }


    // Finds a customer using their username.
    public static Customer findCustomer(String username) {

        for (Customer customer : customers) {

            Credentials credentials = customer.getCredentials();

            if (credentials != null && credentials.getUsername().equals(username)) {
                return customer;
            }
        }
        return null;
    }


    // Returns the currently logged-in customer.
    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    // Returns the currently selected account.
    public static Account getSelectedAccount() {
        return selectedAccount;
    }

    // Sets the currently logged-in customer.
    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    // Sets the currently selected account.
    public static void setSelectedAccount(Account account) {
        selectedAccount = account;
    }

    // Logs out the customer.
    public static void logout() {
        currentCustomer = null;
        selectedAccount = null;
    }
}