package client.controllers;

import client.session.ClientSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import shared.models.User;

import java.io.IOException;

public class MainController {

    @FXML private VBox container;

    @FXML
    private void initialize() {
        Home();
    }

    public void displayFollowersAndFollowings(User user, boolean onFollowers) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("followers&followings.fxml"));

            HBox fBox = loader.load();

            FollowController controller = loader.getController();
            controller.setOnFollowers(onFollowers);
            controller.setUser(user);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(fBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void anyProfile(User u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));

            HBox uBox = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(u);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(uBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Profile() {
        anyProfile(ClientSession.getUser());
    }

    @FXML
    private void Home() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));

            HBox hBox = loader.load();

            HomeController controller = loader.getController();
            controller.setMainController(this);
            controller.setUser(ClientSession.getUser());

            container.getChildren().clear();
            container.getChildren().add(hBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Explore() {
        //
    }

    @FXML
    private void Post() {
        //
    }

    @FXML
    private void Account() {
        //
    }
}
