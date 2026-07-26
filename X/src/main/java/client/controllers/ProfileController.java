package client.controllers;

import client.session.Client;
import client.utils.DateFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import server.database.daos.FollowDAO;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import shared.models.User;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML private ImageView banner;
    @FXML private ImageView profile;

    @FXML private Button editProfileBTN;
    @FXML private Button followBTN;

    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private Label bio;
    @FXML private Label dateOfJoining;

    @FXML private Label tweetsCount;
    @FXML private Label FollowingBTN;
    @FXML private Label FollowerBTN;

    @FXML private Button posts;
    @FXML private Button replies;
    @FXML private Button media;
    @FXML private Button likes;

    @FXML private VBox tweetsContainer;

    private final String defaultProfile = "/images/default_profile.png";
    private final String defaultBanner = "/images/default_banner.png";

    private final FollowDAO followDAO = new FollowDAO();

    private static MainController mainController;

    private TweetDao tweetDao = new TweetDao();

    private User user;

    public void setUser(User u) { this.user = u; }

    public void setMainController(MainController mc) { mainController = mc; }

    @FXML
    private void initialize() {

        //set data of user

        String profile_addresss;
        if (user.getProfilePic() != null) profile_addresss = user.getProfilePic();
        else profile_addresss = defaultProfile;
        Image profile_pic = new Image(getClass().getResourceAsStream(profile_addresss));
        profile.setImage(profile_pic);

        String banner_address;
        if (user.getBannerPic() != null) banner_address = user.getBannerPic();
        else banner_address = defaultBanner;
        Image banner_pic = new Image(getClass().getResourceAsStream(banner_address));
        banner.setImage(banner_pic);

        displayName.setText(user.getDisplayName());
        username.setText(user.getUsername());
        bio.setText(user.getBio());
        dateOfJoining.setText( "Joined " + DateFormatter.joiningDate(user.getCreatedAt()));

        //set text for tweets, followings & followers (include counts)
        int tweetC = tweetDao.getTweetByUserId(user.getId()).size();
        int followerC = followDAO.getFollowerCount(user.getId());
        int followingC = followDAO.getFollowingCount(user.getId());
        tweetsCount.setText(tweetC + "Tweets");
        FollowerBTN.setText(followerC + " Followers");
        FollowingBTN.setText(followingC + " Following");

        if (user.equals(Client.getUser())) {
            editProfileBTN.setVisible(true);
            followBTN.setVisible(false);
        }
        else {
            editProfileBTN.setVisible(false);
            followBTN.setVisible(true);
            if (followDAO.isFollowing(Client.getUser().getId(), user.getId())) {
                followBTN.setText("Unfollow");
            }
            else {
                followBTN.setText("Follow");
            }
        }

        //set style (the underline) ?????????????????

        //display posts
        displayPosts();
    }

    @FXML
    private void editProfile() {
        //display the mini window
        mainController.setEditWindow();
    }

    @FXML
    private void follow() {

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

    @FXML
    private void displayFollowings() {
        //switch the box
        mainController.displayFollowersAndFollowings(user, false);
    }

    @FXML
    private void displayFollowers() {
        //switch the box
        mainController.displayFollowersAndFollowings(user, true);
    }

    @FXML
    private void displayPosts() {

        //switch the tweets
        tweetsContainer.getChildren().clear();
        List<Tweet> posts = tweetDao.getTweetByUserId(user.getId());
        for (Tweet t : posts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tweet-card.fxml"));

                HBox tweetBox = loader.load();

                TweetController controller = loader.getController();
                controller.setTweet(t);
                controller.setMainController(mainController);

                tweetsContainer.getChildren().add(tweetBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //change style (the underline) ????????????????
    }
}
