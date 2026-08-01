package client.controllers;

import client.CurrentClient;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import server.database.daos.FollowDAO;
import server.database.daos.UserDao;
import shared.models.User;

public class UserCardController {

    @FXML private ImageView profile;
    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private Label bio;

    @FXML private Button followBTN;

    private User user;

    private static MainController mainController;

    private final FollowDAO followDAO = new FollowDAO();

    public UserCardController(MainController mc, User u) {
        mainController = mc;
        this.user = u;
    }

    @FXML
    private void initialize() {

        //set data of user
        profile.setImage(ImageLoader.getProfileImage(user.getProfilePic()));
        displayName.setText(user.getDisplayName());
        username.setText("@" + user.getUsername());
        bio.setText(user.getBio());

        if (user.equals(CurrentClient.getUser())) {
            followBTN.setVisible(false);
            followBTN.setManaged(false);
        }
        else {
            followBTN.setManaged(true);
            followBTN.setVisible(true);
            if (followDAO.isFollowing(CurrentClient.getUser().getId(), user.getId())) {
                followBTN.setText("Unfollow");
            }
            else {
                followBTN.setText("Follow");
            }
        }
    }

    @FXML
    private void displayProfile() {
        mainController.anyProfile(user);
    }

    @FXML
    private void follow() {
        FollowDAO followDAO = new FollowDAO();

        if (followBTN.getText().equals("Follow")) {
            //follow the user
            boolean b = followDAO.follow(CurrentClient.getUser().getId(), user.getId());
            if (b) followBTN.setText("Unfollow");
        }
        else {
            //unfollow the user
            boolean b = followDAO.unfollow(CurrentClient.getUser().getId(), user.getId());
            if (b) followBTN.setText("Follow");
        }
    }
}
