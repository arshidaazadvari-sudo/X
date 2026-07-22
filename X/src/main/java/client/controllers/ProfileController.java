package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ProfileController {
    @FXML private ImageView banner;
    @FXML private Circle profilePicture;

    @FXML private Button editProfileBTN;
    @FXML private Button followBTN;

    @FXML private Label name;
    @FXML private Label username;
    @FXML private Label bio;
    @FXML private Label dateOfJoining;

    @FXML private Label FollowingBTN;
    @FXML private Label FollowerBTN;

    @FXML private Button posts;
    @FXML private Button replies;
    @FXML private Button media;
    @FXML private Button likes;

    @FXML private VBox tweetsContainer;

    @FXML
    private void initialize() {
        //
    }

    @FXML
    private void editProfile() {
        //
    }

    @FXML
    private void follow() {
        //
    }

    @FXML
    private void displayFollowings() {
        //switch scene
    }

    @FXML
    private void displayFollowers() {
        //switch scene
    }

    @FXML
    private void displayPosts() {
        //
    }

    @FXML
    private void displayReplies() {
        //
    }

    @FXML
    private void displayMedia() {
        //
    }

    @FXML
    private void displayLikes() {
        //
    }
}
