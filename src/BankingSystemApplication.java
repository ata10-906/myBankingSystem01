import javafx.application.Application;
import javafx.stage.Stage;

public class BankingSystemApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Banking System");

        ViewNavigator.setStage(primaryStage);

        ViewNavigator.show("LoginView.fxml");
    }

    public static void main (String[] args) {
        launch(args);
    }
}


