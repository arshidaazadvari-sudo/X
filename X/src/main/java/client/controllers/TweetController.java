package client.controllers;

import client.network.ServerConnection;
import client.session.Client;
import client.utils.DateFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.database.daos.*;
import shared.models.Tweet;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweetController {

    @FXML private HBox mediaBox;
    @FXML private ImageView profilePicture;
    @FXML private Text name;
    @FXML private Text username;
    @FXML private Text postingDate;
    @FXML private Button followBTN;
    @FXML private TextFlow tweetText;
    @FXML private Label replyCount;
    @FXML private Button repost;
    @FXML private Label repostCount;
    @FXML private Button like;
    @FXML private Label likeCount;

    @FXML private TextFlow replyToText;
    @FXML private Text replyToUsername;

    private Tweet givenTweet;
    private Tweet realTweet;
    private static MainController mainController;

    private String defaultProfile = "/images/default_profile.png";
    private LikeDAO likeDAO = new LikeDAO();
    private ReplyDAO replyDAO = new ReplyDAO();
    private TweetDao tweetDao = new TweetDao();

    public void setTweet(Tweet tweet) { this.givenTweet = tweet; }

    public Tweet getTweet() { return realTweet; }

    public void setMainController(MainController mc) { mainController = mc; }

    public MainController getMainController() { return mainController;}

    @FXML
    private void initialize() {

        Integer tweetId = givenTweet.getRetweetOfTweetId();
        while (tweetId != null) {
            Integer temp = tweetDao.getTweetById(tweetId).getRetweetOfTweetId();
            tweetId = temp;
        }

        if (tweetId != null) realTweet = tweetDao.getTweetById(tweetId);
        else realTweet = givenTweet;


        if (realTweet.getReplyToTweetId() != null) {
            replyToText.setVisible(true);
            replyToText.setManaged(true);
            replyToUsername.setText("Reply to @" + tweetDao.getTweetById(realTweet.getReplyToTweetId()).getUsername());
        }
        else {
            replyToText.setVisible(false);
            replyToText.setManaged(false);
        }

        UserDao userDao = new UserDao();

        String miniPro_address;
        miniPro_address = userDao.getUserById(realTweet.getUserId()).getProfilePic();
        if (miniPro_address == null) miniPro_address = defaultProfile;
        Image miniPro_pic = new Image(getClass().getResourceAsStream(miniPro_address));
        profilePicture.setImage(miniPro_pic);

        //?????????????????????????????
        replyCount.setText(" " + realTweet.getRepliesCount());
        likeCount.setText(" " + realTweet.getLikesCount());
        //repostCount.setText(" " + );
        name.setText(realTweet.getDisplayName());
        username.setText(" @" + realTweet.getUsername());
        postingDate.setText(" . " + DateFormatter.postingDate(realTweet.getCreatedAt()));

        boolean b1 = likeDAO.isLikedByUser(realTweet.getUserId(), realTweet.getId());
        //if (b1) like.setStyle();
        //else like.setStyle();
        //boolean b2 = ;
        //if(b2) repost.setStyle();
        //else repost.setStyle();

        Text t = new Text(realTweet.getContent());
        tweetText = new TextFlow(t);
        //linked hashtags & mentions (Bonus)

        //mediaBox
        List<Image> images = new ArrayList<>();
        for (String s : realTweet.getMediaUrls()) {
            //media ???????????????????
        }
        List<Button> btns = mainController.displayMedia(images, mediaBox);
        for (Button b : btns) {
            b.setManaged(false);
            b.setVisible(false);
        }
    }

    @FXML
    private void tweetPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tweet-page.fxml"));

            HBox tBox = loader.load();

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
            boolean b = followDAO.follow(Client.getUser().getId(), realTweet.getUserId());
            if (b) followBTN.setText("Unfollow");
        }
        else {
            //unfollow the user
            boolean b = followDAO.unfollow(Client.getUser().getId(), realTweet.getUserId());
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
    public void repost() {
        ObjectNode payload = ServerConnection.mapper.createObjectNode();
        ObjectNode retweet = ServerConnection.mapper.createObjectNode();

        payload.put("originalTweetId", realTweet.getId());
        payload.put("userId", Client.getUser().getId());

        retweet.put("type", "RETWEET");
        retweet.set("payload", payload);
        Client.getConnection().send(retweet.toString());

        //repost.setStyle(); ?????????????????
    }

    @FXML
    public void like() {
        boolean b1 = likeDAO.isLikedByUser(realTweet.getUserId(), realTweet.getId());
        if (b1) {
            boolean b2 = likeDAO.unlike(realTweet.getUserId(), realTweet.getId());
            //if (b2) like.setStyle(); ???????????????????
        }
        else {
            boolean b2 = likeDAO.like(realTweet.getUserId(), realTweet.getId());
            //if (b2) like.setStyle(); ????????????????????
        }
    }

    @FXML
    public void showRepliedTweet() {
        try {
            FXMLLoader loader1 = new FXMLLoader(HomeController.class.getResource("tweet-card.fxml"));

            TweetController controller1 = loader1.getController();
            controller1.setTweet(tweetDao.getTweetById(realTweet.getReplyToTweetId()));
            controller1.setMainController(mainController);


            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("tweet-page.fxml"));

            HBox tBox = loader2.load();

            TweetPageController controller2 = loader2.getController();
            controller2.setTweetController(controller1);

            mainController.getContainer().getChildren().clear();
            mainController.getContainer().getChildren().add(tBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
