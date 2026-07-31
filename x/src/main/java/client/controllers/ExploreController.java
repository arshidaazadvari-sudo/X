package client.controllers;

import client.CurrentClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.HashtagDAO;
import server.database.daos.TweetDao;
import server.database.daos.UserDao;
import shared.models.Tweet;
import shared.models.User;

import java.io.IOException;
import java.util.List;

public class ExploreController {

    @FXML private Button returnBTN;
    @FXML private TextField searchText;
    @FXML private VBox resultsContainer;
    @FXML private VBox trendingHashtags;

    @FXML private FontIcon returnIcon;
    @FXML private FontIcon searchIcon;

    private static String givenHashtag;

    private MainController mainController;

    //public void setMainController(MainController mc) { mainController = mc; }

    //public static void setHashtag(String s) { givenHashtag = s; }

    public ExploreController(MainController mc) {
        this.mainController = mc;
        givenHashtag = null;
    }
    public ExploreController(MainController mc, String s) {
        this.mainController = mc;
        givenHashtag = s;
    }

    @FXML
    private void initialize() {

        //icons
        returnIcon.setIconCode(FontAwesomeSolid.ARROW_LEFT);
        searchIcon.setIconCode(FontAwesomeSolid.SEARCH);

        //automatically search the given mention or hashtag if there's any; if not, display trending hashtags

        if (givenHashtag != null) {
            searchText.setText("#" + givenHashtag);
            search();
            givenHashtag = null;
        }
        else {
            HashtagDAO hD = new HashtagDAO();
            List<String> trending= hD.getTrendingHashtags(10);
            for (String s : trending) {

                VBox vb = new VBox();
                vb.setOnMouseClicked(mouseEvent -> {
                    givenHashtag = s;
                    mainController.Explore();
                });
                Text t = new Text("#" + s);

                t.setStyle("-fx-font-weight: bold;");
                t.getStyleClass().add("primary-t");

                vb.getChildren().add(t);

                trendingHashtags.getChildren().add(vb);
            }

            trendingHashtags.setManaged(true);
            trendingHashtags.setVisible(true);
            resultsContainer.setManaged(false);
            resultsContainer.setVisible(false);

            returnBTN.setVisible(false);
        }
    }

    @FXML
    private void search() {
        trendingHashtags.setManaged(false);
        trendingHashtags.setVisible(false);
        resultsContainer.setManaged(true);
        resultsContainer.setVisible(true);

        returnBTN.setVisible(true);

        resultsContainer.getChildren().clear();

        String search = searchText.getText();

        if (search.startsWith("#")) {
            displayHTweets(search);
        }
        else if (search.startsWith("@")) {
            displayUsers(search);
        }
        else {
            displayTweets(search);
            displayHTweets(search);
            displayUsers(search);
        }
    }

    @FXML
    private void Return() {
        mainController.Explore();
    }

    private void displayUsers(String s) {
        UserDao userDao = new UserDao();
        List<User> users = userDao.searchUser(s);

        for (User u : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/user-card.fxml"));

                UserCardController controller = new UserCardController(mainController, CurrentClient.getUser());
                loader.setController(controller);

                VBox uBox = loader.load();
                
                resultsContainer.getChildren().add(uBox);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    private void displayTweets(String s) {
        TweetDao tweetDao = new TweetDao();
        List<Tweet> tweets = tweetDao.findTweetsByKeyword(s, 40);

        for (Tweet t : tweets) {
            try {
                FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

                TweetController controller = new TweetController(mainController, t);
                loader.setController(controller);

                VBox tweetBox = loader.load();

                resultsContainer.getChildren().add(tweetBox);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    private void displayHTweets(String s) {
        HashtagDAO hashtagDAO = new HashtagDAO();
        List<Tweet> tweets = hashtagDAO.findTweetsByHashtag(s, 40);

        for (Tweet t : tweets) {
            try {
                FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

                TweetController controller = new TweetController(mainController, t);
                loader.setController(controller);

                VBox tweetBox = loader.load();

                resultsContainer.getChildren().add(tweetBox);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }
}
