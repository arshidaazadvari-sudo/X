package client.controllers;

import client.network.ServerConnection;
import client.CurrentClient;
import client.utils.DateFormatter;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.*;
import shared.models.Tweet;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetController {

    @FXML private HBox mediaBox;
    @FXML private ImageView profilePicture;
    @FXML private Text name;
    @FXML private Text username;
    @FXML private Text postingDate;

    @FXML private Button followBTN;
    @FXML private Button deleteBTN;

    @FXML private TextFlow tweetText;
    @FXML private Button reply;
    @FXML private Button repost;
    @FXML private Button like;

    @FXML private FontIcon deleteIcon;
    @FXML private FontIcon repliedIcon;
    @FXML private FontIcon replyIcon;
    @FXML private FontIcon repostIcon;
    @FXML private FontIcon likeIcon;

    @FXML private TextFlow replyToText;
    @FXML private Label replyToUsername;

    private Tweet givenTweet;
    private Tweet realTweet;
    private static MainController mainController;

    private LikeDAO likeDAO = new LikeDAO();
    private ReplyDAO replyDAO = new ReplyDAO();
    private TweetDao tweetDao = new TweetDao();

    public void setTweet(Tweet tweet) { this.givenTweet = tweet; }

    public Tweet getTweet() { return realTweet; }

    public void setMainController(MainController mc) { mainController = mc; }

    public MainController getMainController() { return mainController;}

    @FXML
    private void initialize() {

        //find the real tweet owner

        Integer tweetId = givenTweet.getRetweetOfTweetId();
        while (tweetId != null) {
            Integer temp = tweetDao.getTweetById(tweetId).getRetweetOfTweetId();
            tweetId = temp;
        }

        if (tweetId != null) realTweet = tweetDao.getTweetById(tweetId);
        else realTweet = givenTweet;


        //icons

        deleteIcon.setIconCode(FontAwesomeSolid.WINDOW_CLOSE);
        repliedIcon.setIconCode(FontAwesomeSolid.REPLY);
        replyIcon.setIconCode(FontAwesomeRegular.COMMENT);
        likeIcon.setIconCode(FontAwesomeRegular.HEART);
        repostIcon.setIconCode(FontAwesomeSolid.RETWEET);

        boolean b1 = likeDAO.isLikedByUser(realTweet.getUserId(), realTweet.getId());
        if (b1) {
            likeIcon.setIconCode(FontAwesomeSolid.HEART);
            likeIcon.getStyleClass().clear();
            likeIcon.getStyleClass().add("red-like-icon");
        }

        boolean b2 = true;
        // isRepostedByUser  ?????????????????????
        if (b2) {
            repostIcon.getStyleClass().clear();
            repostIcon.getStyleClass().add("green-repost-icon");
        }


        //name the account you're replying to (if you are)

        if (realTweet.getReplyToTweetId() != null) {
            replyToUsername.setText("Reply to @" + tweetDao.getTweetById(realTweet.getReplyToTweetId()).getUsername());
            replyToText.setVisible(true);
        } else {
            replyToText.setVisible(false);
        }

        UserDao userDao = new UserDao();

        //set initial data of the tweet

        profilePicture.setImage(ImageLoader.getProfileImage(userDao.getUserById(realTweet.getUserId()).getProfilePic()));

        int r = tweetDao.getRepliesForTweet(realTweet.getId()).size();
        reply.setText(String.valueOf(r));
        like.setText(String.valueOf(realTweet.getLikesCount()));
        //repost.setText(); ??????????????????????
        name.setText(realTweet.getDisplayName());
        username.setText(" @" + realTweet.getUsername());
        postingDate.setText(" . " + DateFormatter.postingDate(realTweet.getCreatedAt()));

        Text t = new Text(realTweet.getContent());
        tweetText = new TextFlow(t);

        //mediaBox
        List<String> images = new ArrayList<>(Arrays.asList(realTweet.getMediaUrls()));
        List<Button> btns = mainController.displayMedia(images, mediaBox);
        for (Button b : btns) {
            b.setManaged(false);
            b.setVisible(false);
        }

        if (realTweet.getUserId() == CurrentClient.getUser().getId()) {
            followBTN.setManaged(false);
            followBTN.setVisible(false);
        }

        deleteBTN.setManaged(false);
        deleteBTN.setVisible(false);
    }

    @FXML
    private void tweetPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/tweet-page.fxml"));

            VBox tBox = loader.load();

            TweetPageController controller = loader.getController();
            controller.setTweetController(this);

            mainController.getContainer().getChildren().clear();
            mainController.getContainer().getChildren().add(tBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void profile() {
        UserDao userDao = new UserDao();
        mainController.anyProfile(userDao.getUserById(realTweet.getUserId()));
    }

    @FXML
    private void follow() {
        FollowDAO followDAO = new FollowDAO();
        if (followBTN.getText().equals("Follow")) {
            //follow the user
            boolean b = followDAO.follow(CurrentClient.getUser().getId(), realTweet.getUserId());
            if (b) followBTN.setText("Unfollow");
        }
        else {
            //unfollow the user
            boolean b = followDAO.unfollow(CurrentClient.getUser().getId(), realTweet.getUserId());
            if (b) followBTN.setText("Follow");
        }
    }

    @FXML
    public void reply() {
        mainController.setRTweet(realTweet);
        mainController.setIsReplying(true);
        mainController.PostWindow();
    }

    @FXML
    public boolean repost() {
        repostIcon.getStyleClass().clear();

        boolean b1 = true;
        //b1 = isRepostedByUser ?????????????????
        if (b1) {
            ObjectNode payload = ServerConnection.mapper.createObjectNode();
            ObjectNode retweet = ServerConnection.mapper.createObjectNode();

            payload.put("tweetId", givenTweet.getId());
            payload.put("userId", CurrentClient.getUser().getId());

            retweet.put("type", "DELETE_TWEET");
            retweet.set("payload", payload);
            CurrentClient.getConnection().send(retweet.toString());

            repostIcon.getStyleClass().add("gray-repost-icon");
        }
        else {
            ObjectNode payload = ServerConnection.mapper.createObjectNode();
            ObjectNode retweet = ServerConnection.mapper.createObjectNode();

            payload.put("originalTweetId", realTweet.getId());
            payload.put("userId", CurrentClient.getUser().getId());

            retweet.put("type", "RETWEET");
            retweet.set("payload", payload);
            CurrentClient.getConnection().send(retweet.toString());

            repost.getStyleClass().add("green-repost-icon");
        }

        return !b1;
    }

    @FXML
    public boolean like() {
        boolean b1 = likeDAO.isLikedByUser(realTweet.getUserId(), realTweet.getId());
        boolean b2;
        if (b1) {
            b2 = likeDAO.unlike(realTweet.getUserId(), realTweet.getId());
            if (b2) {
                likeIcon.setIconCode(FontAwesomeRegular.HEART);
                likeIcon.getStyleClass().clear();
                likeIcon.getStyleClass().add("gray-like-icon");
            }
        }
        else {
            b2 = likeDAO.like(realTweet.getUserId(), realTweet.getId());
            if (b2) {
                likeIcon.setIconCode(FontAwesomeSolid.HEART);
                likeIcon.getStyleClass().clear();
                likeIcon.getStyleClass().add("red-like-icon");
            }
        }

        return !(b1 && b2);
    }

    @FXML
    public void showRepliedTweet() {
        try {
            FXMLLoader loader1 = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

            TweetController controller1 = loader1.getController();
            controller1.setTweet(tweetDao.getTweetById(realTweet.getReplyToTweetId()));
            controller1.setMainController(mainController);


            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxmls/tweet-page.fxml"));

            VBox tBox = loader2.load();

            TweetPageController controller2 = loader2.getController();
            controller2.setTweetController(controller1);

            mainController.getContainer().getChildren().clear();
            mainController.getContainer().getChildren().add(tBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void MEntered() {
        if (realTweet.getUserId() == CurrentClient.getUser().getId()) {
            deleteBTN.setManaged(true);
            deleteBTN.setVisible(true);
        }
    }

    @FXML
    private void MExited() {
        deleteBTN.setManaged(false);
        deleteBTN.setVisible(false);
    }

    @FXML
    private void delete() {
        mainController.displayDeleteWindow(realTweet);
    }
}
