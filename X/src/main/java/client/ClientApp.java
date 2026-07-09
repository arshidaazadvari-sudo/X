package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700); //________________
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        stage.setTitle("X");
        stage.setScene(scene);

        //Image icon = new Image(getClass().getResourceAsStream("X.png"));
        //stage.getIcons().add(icon);

        stage.show();
    }
}
