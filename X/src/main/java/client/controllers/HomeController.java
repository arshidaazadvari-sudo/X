package client.controllers;

import client.network.ResponseListener;
import client.network.ServerConnection;
import client.session.ClientSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import shared.models.User;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class HomeController {

    @FXML private HBox TLTweetCard;

    @FXML private ImageView miniProfilePicture;
    @FXML private TextArea postText;

    @FXML private Button mediaBTN;
    @FXML private Button postBTN;

    @FXML private HBox mediaBox;

    @FXML private static VBox tweetsContainer;

    private List<Image> images;

    private MainController mainController;

    private User user;

    public void setMainController(MainController mc) { this.mainController = mc; }

    public void setUser(User u) { this.user = u; }

    public static void addNewTweets(Tweet t) {
        try {
            FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("tweet-card.fxml"));

            HBox tweetBox = loader.load();

            TweetController controller = loader.getController();
            controller.setTweet(t);

            tweetsContainer.getChildren().add(tweetBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        //??????????
        mediaBox.setVisible(false);
        mediaBox.setManaged(false);

        tweetsContainer.getChildren().clear();
        TweetDao tweetDao = new TweetDao();
        List<Tweet> posts = tweetDao.getFeedForUser(user.getId(), 100);
        for (Tweet t : posts) {
            addNewTweets(t);
        }

        ClientSession.setOnHomePage(true);
    }

    @FXML
    private void showMore() {
        //
    }

    @FXML
    private void displayProfile() {
        mainController.anyProfile(ClientSession.getUser());
    }

    @FXML
    private void Media() {
        if (images.size() < 4) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(miniProfilePicture.getScene().getWindow());

            if (file != null) {
                Image image = new Image(file.toURI().toString());
                images.add(image);
            }

            mainController.displayDraftMedia(images, mediaBox);
        }
    }

    @FXML
    private void Post() {
        mainController.posting(images, postText.getText());
    }
}
