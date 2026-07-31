package client.controllers;

import client.CurrentClient;
import client.utils.DateFormatter;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.FollowDAO;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import shared.models.User;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML private ImageView banner;
    @FXML private ImageView profile;
    @FXML private HBox btnBox;

    @FXML private Button editProfileBTN;
    @FXML private Button followBTN;

    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private Label bio;
    @FXML private Label dateOfJoining;

    @FXML private Label tweetsCount;
    @FXML private Label FollowingBTN;
    @FXML private Label FollowerBTN;

    @FXML private Rectangle pIndicator;
    @FXML private Rectangle rIndicator;
    @FXML private Rectangle mIndicator;

    @FXML private HBox noPosts;

    @FXML private VBox tweetsContainer;

    @FXML private FontIcon joiningDateIcon;

    private final FollowDAO followDAO = new FollowDAO();

    private TweetDao tweetDao = new TweetDao();

    private List<Tweet> allPosts;

    private static MainController mainController;

    private User user;

    //public void setUser(User u) { this.user = u; }

    //public void setMainController(MainController mc) { mainController = mc; }

    public ProfileController(MainController mc, User u) {
        mainController = mc;
        this.user = u;
    }

    @FXML
    private void initialize() {

        StackPane.setMargin(profile, new Insets(0, 0, 10, 20));
        StackPane.setMargin(btnBox, new Insets(0, 20, 10, 0));

        //icon
        joiningDateIcon.setIconCode(FontAwesomeSolid.CALENDAR);

        //get all the user's posts
        allPosts = tweetDao.getTweetByUserId(user.getId());

        //set data of user

        Image profile_pic = ImageLoader.getProfileImage(user.getProfilePic());
        profile.setImage(profile_pic);

        Image banner_pic = ImageLoader.getBannerImage(user.getBannerPic());
        banner.setImage(banner_pic);

        displayName.setText(user.getDisplayName());
        username.setText(user.getUsername());
        bio.setText(user.getBio());
        //dateOfJoining.setText( "Joined " + DateFormatter.joiningDate(user.getCreatedAt()));

        //set text for tweets, followings & followers (include counts)
        int tweetC = tweetDao.getTweetByUserId(user.getId()).size();
        int followerC = followDAO.getFollowerCount(user.getId());
        int followingC = followDAO.getFollowingCount(user.getId());
        tweetsCount.setText(tweetC + "Tweets");
        FollowerBTN.setText(followerC + " Followers");
        FollowingBTN.setText(followingC + " Following");

        if (user.equals(CurrentClient.getUser())) {
            editProfileBTN.setVisible(true);
            editProfileBTN.setManaged(true);
            followBTN.setVisible(false);
            followBTN.setManaged(false);
        }
        else {
            editProfileBTN.setVisible(false);
            editProfileBTN.setManaged(false);
            followBTN.setVisible(true);
            followBTN.setManaged(true);
            if (followDAO.isFollowing(CurrentClient.getUser().getId(), user.getId())) {
                followBTN.setText("Unfollow");
            }
            else {
                followBTN.setText("Follow");
            }
        }

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
            boolean b = followDAO.follow(CurrentClient.getUser().getId(), user.getId());
            if (b) followBTN.setText("Unfollow");
        }
        else {
            //unfollow the user
            boolean b = followDAO.unfollow(CurrentClient.getUser().getId(), user.getId());
            if (b) followBTN.setText("Follow");
        }
    }

    @FXML
    private void displayFollowings() {
        mainController.displayFollowersAndFollowings(user, false);
    }

    @FXML
    private void displayFollowers() {
        mainController.displayFollowersAndFollowings(user, true);
    }

    @FXML
    private void displayPosts() {

        tweetsContainer.getChildren().clear();

        for (Tweet t : allPosts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/tweet-card.fxml"));

                TweetController controller = new TweetController(mainController, t);
                loader.setController(controller);

                VBox tweetBox = loader.load();

                tweetsContainer.getChildren().add(tweetBox);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        //display "No posts yet. "
        if (tweetsContainer.getChildren().isEmpty()) {
            tweetsContainer.setManaged(false);
            tweetsContainer.setVisible(false);
            noPosts.setManaged(true);
            noPosts.setVisible(true);
        }

        //change the visible indicator
        pIndicator.setVisible(true);
        rIndicator.setVisible(false);
        mIndicator.setVisible(false);
    }

    @FXML
    private void displayReplies() {

        tweetsContainer.getChildren().clear();

        for (Tweet t : allPosts) {
            if ((t.getReplyToTweetId() != null)&&(t.getReplyToTweetId() != 0)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/tweet-card.fxml"));

                    TweetController controller = new TweetController(mainController, t);
                    loader.setController(controller);

                    VBox tweetBox = loader.load();

                    tweetsContainer.getChildren().add(tweetBox);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        }

        //display "No posts yet. "
        if (tweetsContainer.getChildren().isEmpty()) {
            tweetsContainer.setManaged(false);
            tweetsContainer.setVisible(false);
            noPosts.setManaged(true);
            noPosts.setVisible(true);
        }

        //change the visible indicator
        pIndicator.setVisible(false);
        rIndicator.setVisible(true);
        mIndicator.setVisible(false);
    }

    @FXML
    private void displayMedia() {

        tweetsContainer.getChildren().clear();

        for (Tweet t : allPosts) {
            if (t.getMediaUrls() != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/tweet-card.fxml"));

                    TweetController controller = new TweetController(mainController, t);
                    loader.setController(controller);

                    VBox tweetBox = loader.load();

                    tweetsContainer.getChildren().add(tweetBox);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        }

        //display "No posts yet. "
        if (tweetsContainer.getChildren().isEmpty()) {
            tweetsContainer.setManaged(false);
            tweetsContainer.setVisible(false);
            noPosts.setManaged(true);
            noPosts.setVisible(true);
        }

        //change the visible indicator
        pIndicator.setVisible(false);
        rIndicator.setVisible(false);
        mIndicator.setVisible(true);
    }
}
