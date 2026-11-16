import java.util.ArrayList;
import java.util.List;

public class AppSession {

    //Stores customers while the program is running.
    private static List<Customer> customers = new ArrayList<>();

    private static Customer currentCustomer;
    private static Account selectedAccount;

    //Register a customer in the application.
    public static void addCustomer(Customer customer) {
        customers.add(customer);
    }

    //Find a customer using their username.
    public static Customer findCustomer(String username) {
        for (Customer customer : customers) {
            Credentials credentials = customer.getCredentials();

            if (credentials != null && credentials.getUsername().equals(username)) {
                return customer;
            }
        }

        return null;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static Account getSelectedAccount() {
        return selectedAccount;
    }

    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public static void setSelectedAccount(Account account) {
        selectedAccount = account;
    }

    public static void logout() {
        currentCustomer = null;
        selectedAccount = null;
    }
}
