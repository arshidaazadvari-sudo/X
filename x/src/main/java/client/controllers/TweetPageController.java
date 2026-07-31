package client.controllers;

import client.CurrentClient;
import client.utils.DateFormatter;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.LikeDAO;
import server.database.daos.TweetDao;
import server.database.daos.UserDao;
import shared.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetPageController {

    @FXML private ImageView profilePicture;
    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private TextFlow tweetText;
    @FXML private HBox mediaBox;
    @FXML private Label postingDate;

    @FXML private Button repost;
    @FXML private Button reply;
    @FXML private Button like;

    @FXML private FontIcon repliedIcon;
    @FXML private FontIcon replyIcon;
    @FXML private FontIcon repostIcon;
    @FXML private FontIcon likeIcon;

    @FXML private VBox replyTweetsContainer;

    @FXML private TextFlow replyToText;
    @FXML private Label replyToUsername;

    private TweetController tweetController;
    private Tweet tweet;

    public void setTweetController(TweetController tc) { this.tweetController = tc; }

    @FXML
    private void initialize() {

        UserDao userDao = new UserDao();
        LikeDAO likeDAO = new LikeDAO();

        //icons

        repliedIcon.setIconCode(FontAwesomeSolid.REPLY);
        replyIcon.setIconCode(FontAwesomeRegular.COMMENT);
        repostIcon.setIconCode(FontAwesomeSolid.RETWEET);
        likeIcon.setIconCode(FontAwesomeRegular.HEART);

        boolean b1 = likeDAO.isLikedByUser(tweet.getUserId(), tweet.getId());
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



        CurrentClient.setOnHomePage(false);

        tweet = tweetController.getTweet();

        TweetDao tweetDao = new TweetDao();

        //name the account you're replying to (if you are)

        if (tweet.getReplyToTweetId() != null) {
            replyToUsername.setText("Reply to @" + tweetDao.getTweetById(tweet.getReplyToTweetId()).getUsername());
            replyToText.setVisible(true);
        }
        else {
            replyToText.setVisible(false);
        }

        //set initial data of the tweet

        profilePicture.setImage(ImageLoader.getProfileImage(userDao.getUserById(tweet.getUserId()).getProfilePic()));

        int r = tweetDao.getRepliesForTweet(tweet.getId()).size();
        reply.setText(String.valueOf(r));
        like.setText(String.valueOf(tweet.getLikesCount()));
        //repost.setText(); ?????????????????????
        displayName.setText(tweet.getDisplayName());
        username.setText(" @" + tweet.getUsername());
        postingDate.setText(" . " + DateFormatter.postingDateInPage(tweet.getCreatedAt()));

        Text text = new Text(tweet.getContent());
        tweetText = new TextFlow(text);

        //mediaBox
        List<String> images = new ArrayList<>(Arrays.asList(tweet.getMediaUrls()));
        List<Button> btns = tweetController.getMainController().displayMedia(images, mediaBox);
        for (Button b : btns) {
            b.setManaged(false);
            b.setVisible(false);
        }

        //replyTweetsContainer
        List<Tweet> replies = tweetDao.getRepliesForTweet(tweetController.getTweet().getId());
        for (Tweet rt : replies) {
            VBox vb = new VBox();
            vb.setPrefWidth(600);
            vb.getStyleClass().add("box");
            vb.setStyle("-fx-border-width: 2;");
            try {
                FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

                VBox tweetBox = loader.load();

                TweetController controller = loader.getController();
                controller.setTweet(rt);
                controller.setMainController(tweetController.getMainController());

                vb.getChildren().add(tweetBox);

                List<Tweet> nextRL = tweetDao.getRepliesForTweet(rt.getId());
                while (nextRL.size() == 1) {

                    Tweet nextRT = nextRL.getFirst();

                    try {
                        FXMLLoader loader2 = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

                        VBox tweetBox2 = loader2.load();

                        TweetController controller2 = loader2.getController();
                        controller2.setTweet(nextRT);
                        controller2.setMainController(tweetController.getMainController());

                        vb.getChildren().add(tweetBox2);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }

                    nextRL = tweetDao.getRepliesForTweet(nextRT.getId());
                }

                replyTweetsContainer.getChildren().add(vb);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void profile() {
        tweetController.profile();
    }

    @FXML
    private void reply() {
        tweetController.reply();
    }

    @FXML
    private void repost() {
        boolean b = tweetController.repost();
        repostIcon.getStyleClass().clear();
        if (b) repostIcon.getStyleClass().add("green-repost-icon");
        else repostIcon.getStyleClass().add("gray-repost-icon");
    }

    @FXML
    private void like() {
        boolean b = tweetController.like();
        likeIcon.getStyleClass().clear();
        if (b) {
            likeIcon.setIconCode(FontAwesomeSolid.HEART);
            likeIcon.getStyleClass().add("red-like-icon");
        }
        else {
            likeIcon.setIconCode(FontAwesomeRegular.HEART);
            likeIcon.getStyleClass().add("gray-like-icon");
        }
    }

    @FXML
    private void showRepliedTweet() {
        tweetController.showRepliedTweet();
    }
}
