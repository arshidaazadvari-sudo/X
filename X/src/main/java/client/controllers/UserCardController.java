package client.controllers;

import client.session.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import server.database.daos.FollowDAO;
import shared.models.User;

public class UserCardController {

    @FXML private ImageView profile;
    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private TextArea bio;

    @FXML private Button followBTN;

    private User user;

    private MainController mainController;

    private final String defaultProfile = "/images/default_profile.png";

    private final FollowDAO followDAO = new FollowDAO();

    public void setUser(User u) { this.user = u; }

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    private void initialize() {

        //set data of user

        String profile_addresss;
        if (user.getProfilePic() != null) profile_addresss = user.getProfilePic();
        else profile_addresss = defaultProfile;
        Image profile_pic = new Image(getClass().getResourceAsStream(profile_addresss));
        profile.setImage(profile_pic);

        displayName.setText(user.getDisplayName());
        username.setText("@" + user.getUsername());
        bio.setText(user.getBio());

        if (user.equals(Client.getUser())) {
            followBTN.setVisible(false);
        }
        else {
            followBTN.setVisible(true);
            if (followDAO.isFollowing(Client.getUser().getId(), user.getId())) {
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
            boolean b = followDAO.follow(Client.getUser().getId(), user.getId());
            if (b) followBTN.setText("Unfollow");
        }
        else {
            //unfollow the user
            boolean b = followDAO.unfollow(Client.getUser().getId(), user.getId());
            if (b) followBTN.setText("Follow");
        }
    }
}
