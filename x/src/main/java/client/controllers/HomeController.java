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
import server.database.daos.TweetDao;
import shared.models.Tweet;
import shared.models.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML private HBox TLTweetCard;

    @FXML private ImageView miniProfilePicture;
    @FXML private TextArea postText;

    @FXML private HBox mediaBox;

    @FXML private static VBox tweetsContainer;

    private List<String> images;

    private static MainController mainController;

    private User user;

    public void setMainController(MainController mc) { mainController = mc; }

    public void setUser(User u) { this.user = u; }

    public static void addNewTweets(Tweet t) {

        //display newly published tweets (real-time display of tweets received from server)

        try {
            FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/fxmls/tweet-card.fxml"));

            HBox tweetBox = loader.load();

            TweetController controller = loader.getController();
            controller.setTweet(t);
            controller.setMainController(mainController);

            tweetsContainer.getChildren().add(tweetBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void initialize() {

        //initial values and feed tweets from db

        miniProfilePicture.setImage(ImageLoader.getProfileImage(CurrentClient.getUser().getProfilePic()));

        mediaBox.setVisible(false);
        mediaBox.setManaged(false);

        tweetsContainer.getChildren().clear();
        TweetDao tweetDao = new TweetDao();
        List<Tweet> posts = tweetDao.getFeedForUser(user.getId(), 100);
        for (Tweet t : posts) {
            addNewTweets(t);
        }

        CurrentClient.setOnHomePage(true);
    }

    @FXML
    private void displayProfile() {
        mainController.anyProfile(CurrentClient.getUser());
    }

    @FXML
    private void Media() {

        //choose up to 4 media (image)

        if (images.size() < 4) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(miniProfilePicture.getScene().getWindow());

            String newImage = ImageLoader.tweetImageUploader(file);
            images.add(newImage);

            List<Button> btns = mainController.displayMedia(images, mediaBox);
            mainController.buttonStyling(btns);
        }
    }

    @FXML
    private void Post() {
        mainController.posting(images, postText.getText());
    }
}
