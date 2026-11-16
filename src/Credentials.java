public class Credentials {
     public String username;
     public String password;

     public Credentials(String username, String password) {
         this.username = username;
         this.password = password;
     }

     public boolean verify(String enteredPassword) {
         return password.equals(enteredPassword);
     }

     public String getUsername() {
         return username;
     }

     public String getPassword() {
         return password;
     }

}
