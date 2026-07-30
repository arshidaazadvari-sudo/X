package client.controllers;

import client.CurrentClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import server.database.daos.FollowDAO;
import shared.models.User;

import java.io.IOException;
import java.util.List;

public class FollowController {

    @FXML private Rectangle followerInd;
    @FXML private Rectangle followingInd;

    @FXML private VBox usersContainer;

    @FXML private HBox noFollowings;
    @FXML private HBox noFollowers;

    private final FollowDAO followDAO = new FollowDAO();

    private boolean isOnFollowers;

    private User user;

    private static MainController mainController;

    public void setOnFollowers(boolean b) { this.isOnFollowers = b; }

    public void setUser(User u) { this.user = u; }

    public void setMainController(MainController mc) { mainController = mc; }

    private void displayUsers(List<User> users) {

        usersContainer.getChildren().clear();

        for (User u : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/user-card.fxml"));

                HBox uBox = loader.load();

                UserCardController controller = loader.getController();
                controller.setMainController(mainController);
                controller.setUser(u);

                usersContainer.getChildren().add(uBox);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void initialize() {

        CurrentClient.setOnHomePage(false);

        if (isOnFollowers) {
            followers();
        }
        else {
            following();
        }
    }

    @FXML
    private void followers() {
        List<User> followers = followDAO.getFollowers(user.getId());
        displayUsers(followers);
        isOnFollowers = true;

        //display "No followers"
        if (usersContainer.getChildren().isEmpty()) {
            usersContainer.setManaged(false);
            usersContainer.setVisible(false);
            noFollowers.setManaged(true);
            noFollowers.setVisible(true);
        }

        //change style (the underline)
        followerInd.setVisible(true);
        followingInd.setVisible(false);
    }

    @FXML
    private void following() {
        List<User> following = followDAO.getFollowing(user.getId());
        displayUsers(following);
        isOnFollowers = false;

        //display "No followings"
        if (usersContainer.getChildren().isEmpty()) {
            usersContainer.setManaged(false);
            usersContainer.setVisible(false);
            noFollowings.setManaged(true);
            noFollowings.setVisible(true);
        }

        //change style (the underline)
        followerInd.setVisible(false);
        followingInd.setVisible(true);
    }

    @FXML
    private void goToProfile() {
        mainController.anyProfile(user);
    }
}
