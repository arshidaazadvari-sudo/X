package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import server.database.daos.FollowDAO;
import shared.models.User;

import java.io.IOException;
import java.util.List;

public class FollowController {

    @FXML private Button followersBTN;
    @FXML private Button followingsBTN;
    @FXML private VBox usersContainer;

    private final FollowDAO followDAO = new FollowDAO();

    private boolean isOnFollowers;

    private User user;

    private MainController mainController;

    public void setOnFollowers(boolean b) { this.isOnFollowers = b; }

    public void setUser(User u) { this.user = u; }

    public void setMainController(MainController mc) { this.mainController = mc; }

    private void displayUsers(List<User> users) {
        for (User u : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user-card.fxml"));

                HBox uBox = loader.load();

                UserCardController controller = loader.getController();
                controller.setMainController(mainController);
                controller.setUser(user);

                usersContainer.getChildren().clear();
                usersContainer.getChildren().add(uBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void initialize() {
        if (isOnFollowers) {
            followers();
        }
        else {
            following();
        }
        //set style (the underline) ??????????????
    }

    @FXML
    private void followers() {
        List<User> followers = followDAO.getFollowers(user.getId());
        displayUsers(followers);
        isOnFollowers = true;
        //change style (the underline) ??????????????
    }

    @FXML
    private void following() {
        List<User> following = followDAO.getFollowing(user.getId());
        displayUsers(following);
        isOnFollowers = false;
        //change style (the underline) ??????????????
    }

    @FXML
    private void goToProfile() {
        mainController.anyProfile(user);
    }
}
