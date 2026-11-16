import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class ViewNavigator {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    //Display another FXML screen.
    public static void show(String filename) throws IOException {

        //Locate the FXML file inside the resource folder.
        URL location = ViewNavigator.class.getResource("/" + filename);

        //Checks whether the FXML file exists.
        if (location == null) {
            throw new IOException("FXML file not found: " + filename);
        }

        //Load the FXML file
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();

        //Display the new screen.
        Scene scene = new Scene(root);


        stage.setScene(scene);
        stage.show();
    }
}
