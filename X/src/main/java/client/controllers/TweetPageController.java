package client.controllers;

import client.utils.DateFormatter;
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
import server.database.daos.LikeDAO;
import server.database.daos.ReplyDAO;
import server.database.daos.TweetDao;
import server.database.daos.UserDao;
import shared.models.Reply;
import shared.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweetPageController {

    public Label replyCount;
    public Label repostCount;
    public Label likeCount;
    @FXML private ImageView profilePicture;
    @FXML private Label displayName;
    @FXML private Label username;
    @FXML private TextFlow tweetText;
    @FXML private HBox mediaBox;
    @FXML private Label postingDate;
    @FXML private Button repost;
    @FXML private Button like;
    @FXML private VBox replyTweetsContainer;

    @FXML private TextFlow replyToText;
    @FXML private Text replyToUsername;

    private TweetController tweetController;
    private Tweet tweet;

    private String defaultProfile = "/images/default_profile.png";

    public void setTweetController(TweetController tc) { this.tweetController = tc; }

    @FXML
    private void initialize() {

        tweet = tweetController.getTweet();

        TweetDao tweetDao = new TweetDao();

        if (tweet.getReplyToTweetId() != null) {
            replyToText.setVisible(true);
            replyToText.setManaged(true);
            replyToUsername.setText("Reply to @" + tweetDao.getTweetById(tweet.getReplyToTweetId()).getUsername());
        }
        else {
            replyToText.setVisible(false);
            replyToText.setManaged(false);
        }

        UserDao userDao = new UserDao();
        LikeDAO likeDAO = new LikeDAO();

        String miniPro_address;
        miniPro_address = userDao.getUserById(tweet.getUserId()).getProfilePic();
        if (miniPro_address == null) miniPro_address = defaultProfile;
        Image miniPro_pic = new Image(getClass().getResourceAsStream(miniPro_address));
        profilePicture.setImage(miniPro_pic);

        //?????????????????????????????
        replyCount.setText(" " + tweet.getRepliesCount());
        likeCount.setText(" " + tweet.getLikesCount());
        //repostCount.setText(" " + );
        displayName.setText(tweet.getDisplayName());
        username.setText(" @" + tweet.getUsername());
        postingDate.setText(" . " + DateFormatter.postingDateInPage(tweet.getCreatedAt()));

        boolean b1 = likeDAO.isLikedByUser(tweet.getUserId(), tweet.getId());
        //if (b1) like.setStyle();
        //else like.setStyle();
        //boolean b2 = ;
        //if(b2) repost.setStyle();
        //else repost.setStyle();

        Text text = new Text(tweet.getContent());
        tweetText = new TextFlow(text);
        //linked hashtags & mentions (Bonus)

        //mediaBox
        List<Image> images = new ArrayList<>();
        for (String s : tweet.getMediaUrls()) {
            //media ???????????????????
        }
        List<Button> btns = tweetController.getMainController().displayMedia(images, mediaBox);
        for (Button b : btns) {
            b.setManaged(false);
            b.setVisible(false);
        }

        //replyTweetsContainer
        List<Tweet> replies = tweetDao.getRepliesForTweet(tweetController.getTweet().getId());
        for (Tweet rt : replies) {
            VBox vb = new VBox();
            //set style for vb (the line) ?????????????????????????????
            try {
                FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("tweet-card.fxml"));

                HBox tweetBox = loader.load();

                TweetController controller = loader.getController();
                controller.setTweet(rt);
                controller.setMainController(tweetController.getMainController());

                vb.getChildren().add(tweetBox);

                List<Tweet> nextRL = tweetDao.getRepliesForTweet(rt.getId());
                while (nextRL.size() == 1) {

                    Tweet nextRT = nextRL.getFirst();

                    try {
                        FXMLLoader loader2 = new FXMLLoader(HomeController.class.getResource("tweet-card.fxml"));

                        HBox tweetBox2 = loader2.load();

                        TweetController controller2 = loader2.getController();
                        controller2.setTweet(nextRT);
                        controller2.setMainController(tweetController.getMainController());

                        vb.getChildren().add(tweetBox2);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    nextRL = tweetDao.getRepliesForTweet(nextRT.getId());
                }

                replyTweetsContainer.getChildren().add(vb);

            } catch (IOException e) {
                e.printStackTrace();
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
        tweetController.repost();
    }

    @FXML
    private void like() {
        tweetController.like();
    }

    @FXML
    private void showRepliedTweet() {
        tweetController.showRepliedTweet();
    }
}
