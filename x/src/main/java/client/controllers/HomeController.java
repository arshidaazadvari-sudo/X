package client.controllers;

import client.CurrentClient;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import shared.models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML private static VBox tweetsContainer = new VBox();

    private static MainController mainController;

    public HomeController(MainController mc) {
        mainController = mc;
    }

    public static void addNewTweets(Tweet t) {

        //display newly published tweets (real-time display of tweets received from server)

        try {
            FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

            TweetController controller = new TweetController(mainController, t);
            loader.setController(controller);

            VBox tweetBox = loader.load();

            tweetsContainer.getChildren().add(tweetBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void initialize() {

        TweetDao tweetDao = new TweetDao();
        List<Tweet> posts = tweetDao.getFeedForUser(CurrentClient.getUser().getId(), 100);
        if (posts.isEmpty()) System.out.println("Feed is empty...!");
        for (Tweet t : posts) {
            addNewTweets(t);
            System.out.println("another post");
        }
        System.out.println("posts are in feed");

        CurrentClient.setOnHomePage(true);
    }
}
