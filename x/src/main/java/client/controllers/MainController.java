package client.controllers;

import client.ClientApp;
import client.network.ServerConnection;
import client.CurrentClient;
import client.utils.DateFormatter;
import client.utils.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import server.database.daos.UserDao;
import shared.models.Tweet;
import shared.models.User;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private Button postBTN;

    @FXML private HBox replyingTo;
    @FXML private ImageView R_ProfilePic;
    @FXML private Text R_username;
    @FXML private Text R_name;
    @FXML private Text R_postingDate;
    @FXML private Label R_tweetText;

    @FXML private FontIcon home;
    @FXML private FontIcon explore;
    @FXML private FontIcon profile;
    @FXML private FontIcon logoutIcon;
    @FXML private FontIcon toggle;
    @FXML private FontIcon editCloseIcon;
    @FXML private FontIcon bannerCameraIcon;
    @FXML private FontIcon profileCameraIcon;
    @FXML private FontIcon postCloseIcon;
    @FXML private FontIcon mediaIcon;

    @FXML private Pane overlay1;
    @FXML private VBox editWindow;
    @FXML private Pane overlay2;
    @FXML private VBox discardWindow;
    @FXML private Pane overlay3;
    @FXML private VBox postWindow;
    @FXML private Pane overlay4;
    @FXML private VBox deleteWindow;
    @FXML private Pane overlay5;
    @FXML private VBox logoutWindow;
    @FXML private Pane overlay6;
    @FXML private ImageView fullsizeImage;

    @FXML private ImageView xLogo1;
    @FXML private ImageView xLogo2;

    @FXML private ImageView bannerPic;
    @FXML private ImageView profilePic;
    @FXML private TextField newDisplayName;
    @FXML private TextArea newBio;
    @FXML private Label blankNameMSG;

    @FXML private ImageView miniProfilePic;
    @FXML private TextArea postText;
    @FXML private HBox mediaBox;
    private List<String> images;

    @FXML private VBox container;

    private String newProfilePic;
    private String newBannerPic;

    private boolean isReplying;
    private Tweet R_tweet;

    private Tweet deletingTweet;

    public void setIsReplying(boolean b) { isReplying = b; }
    public void setRTweet(Tweet rt) { R_tweet = rt; }

    public VBox getContainer() { return container; }

    @FXML
    private void initialize() {

        //icons
        home.setIconCode(FontAwesomeSolid.HOME);
        profile.setIconCode(FontAwesomeSolid.USER);
        explore.setIconCode(FontAwesomeSolid.SEARCH);
        logoutIcon.setIconCode(FontAwesomeSolid.SIGN_OUT_ALT);
        toggle.setIconCode(FontAwesomeSolid.SUN);
        editCloseIcon.setIconCode(FontAwesomeRegular.WINDOW_CLOSE);
        bannerCameraIcon.setIconCode(FontAwesomeSolid.CAMERA);
        profileCameraIcon.setIconCode(FontAwesomeSolid.CAMERA);
        postCloseIcon.setIconCode(FontAwesomeRegular.WINDOW_CLOSE);
        mediaIcon.setIconCode(FontAwesomeSolid.IMAGE);

        //initial value for post box
        miniProfilePic.setImage(ImageLoader.getProfileImage(CurrentClient.getUser().getProfilePic()));

        //setting X logo in sidebar & logout window
        xLogo1.setImage(new Image(getClass().getResourceAsStream("/black_logo.png")));
        xLogo1.setImage(new Image(getClass().getResourceAsStream("/black_logo.png")));


        //initial values for edit box

        profilePic.setImage(ImageLoader.getProfileImage(CurrentClient.getUser().getProfilePic()));

        bannerPic.setImage(ImageLoader.getBannerImage(CurrentClient.getUser().getBannerPic()));

        newDisplayName.setText(CurrentClient.getUser().getDisplayName());
        newBio.setText(CurrentClient.getUser().getBio());

        newBannerPic = CurrentClient.getUser().getBannerPic();
        newProfilePic = CurrentClient.getUser().getProfilePic();


        //bio character limit of 160
        newBio.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() < 160) { return change; }
            return null;
        }));
        //name character limit of 50
        newDisplayName.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() < 50) { return change; }
            return null;
        }));

        //invisible at first
        replyingTo.setVisible(false);
        replyingTo.setManaged(false);

        isReplying = false;

        //the default landing page
        Home();
    }

    public void displayFollowersAndFollowings(User user, boolean onFollowers) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/followers&followings.fxml"));

            HBox fBox = loader.load();

            FollowController controller = loader.getController();
            controller.setOnFollowers(onFollowers);
            controller.setUser(user);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(fBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void anyProfile(User u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/profile.fxml"));

            HBox uBox = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(u);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(uBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    //____________________________________________________________
    //sidebar menu on the main scene

    @FXML
    private void Profile() {

        CurrentClient.setOnHomePage(false);
        anyProfile(CurrentClient.getUser());
    }

    @FXML
    private void Home() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/home.fxml"));

            HBox hBox = loader.load();

            HomeController controller = loader.getController();
            controller.setMainController(this);
            controller.setUser(CurrentClient.getUser());

            container.getChildren().clear();
            container.getChildren().add(hBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void Explore() {
        CurrentClient.setOnHomePage(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/explore.fxml"));

            HBox eBox = loader.load();

            ExploreController controller = loader.getController();
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(eBox);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void PostWindow() {

        // the replying-to tweet (if any)
        if (isReplying) {
            replyingTo.setManaged(true);
            replyingTo.setVisible(true);
            postBTN.setText("Reply");

            UserDao userDao = new UserDao();

            R_ProfilePic.setImage(ImageLoader.getProfileImage(userDao.getUserById(R_tweet.getUserId()).getProfilePic()));
            R_username.setText(R_tweet.getUsername());
            R_name.setText(R_tweet.getDisplayName());
            R_postingDate.setText(DateFormatter.postingDate(R_tweet.getCreatedAt()));
            R_tweetText.setText(R_tweet.getContent());
        }

        overlay3.setVisible(true);
        overlay3.setManaged(true);
        postWindow.setVisible(true);
        postWindow.setManaged(true);

        mediaBox.setVisible(false);
        mediaBox.setManaged(false);
    }

    @FXML
    private void LogoutWindow() {
        overlay5.setManaged(true);
        overlay5.setVisible(true);
        logoutWindow.setManaged(true);
        logoutWindow.setVisible(true);
    }

    @FXML
    private void toggleTheme() {
        CurrentClient.setDarkTheme(!CurrentClient.isDark());

        toggle.getScene().getStylesheets().clear();
        if (!CurrentClient.isDark()) {
            //Switch to Light mode
            toggle.getScene().getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
            toggle.setIconCode(FontAwesomeSolid.SUN);
            xLogo1.setImage(new Image(getClass().getResourceAsStream("/black_logo.png")));
            xLogo2.setImage(new Image(getClass().getResourceAsStream("/black_logo.png")));
        }
        else {
            //Switch to Dark mode
            toggle.getScene().getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            toggle.setIconCode(FontAwesomeSolid.MOON);
            xLogo1.setImage(new Image(getClass().getResourceAsStream("/white_logo.png")));
            xLogo2.setImage(new Image(getClass().getResourceAsStream("/white_logo.png")));
        }
    }

    //____________________________________________________________
    //edit profile window

    public void setEditWindow() {
        overlay1.setVisible(true);
        editWindow.setVisible(true);
    }

    @FXML
    private void closeEditWindow() {
        if (CurrentClient.getUser().getBannerPic().equals(newBannerPic) &&
                CurrentClient.getUser().getProfilePic().equals(newProfilePic) &&
                CurrentClient.getUser().getDisplayName().equals(newDisplayName.getText()) &&
                CurrentClient.getUser().getBio().equals(newBio.getText())) {
            overlay1.setVisible(false);
            overlay1.setManaged(false);
            editWindow.setVisible(false);
            editWindow.setManaged(false);
        }
        else {
            overlay2.setManaged(true);
            overlay2.setVisible(true);
            discardWindow.setManaged(true);
            discardWindow.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() {
        if (newDisplayName.getText() != null) {

            blankNameMSG.setVisible(false);

            User updatedUser = new User(CurrentClient.getUser().getUsername(),
                    CurrentClient.getUser().getEmail(),
                    CurrentClient.getUser().getPasswordHash());
            updatedUser.setId(CurrentClient.getUser().getId());
            updatedUser.setProfilePic(newProfilePic);
            updatedUser.setBannerPic(newBannerPic);
            updatedUser.setDisplayName(newDisplayName.getText());
            updatedUser.setBio(newBio.getText());
            UserDao userDao = new UserDao();
            boolean b = userDao.updateUser(updatedUser);
            if (b) {
                User u = userDao.getUserById(CurrentClient.getUser().getId());
                CurrentClient.setUser(u);
            } else {
                System.out.println("Failed to save the changes. ");
            }

            overlay1.setManaged(false);
            overlay1.setVisible(false);
            editWindow.setManaged(false);
            editWindow.setVisible(false);
        }
        else {
            newDisplayName.setStyle("-fx-border-color: rgb(244 33 44);");
            blankNameMSG.setVisible(true);
        }
    }

    @FXML
    private void chooseBannerPic() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(bannerPic.getScene().getWindow());

        newBannerPic = ImageLoader.bannerImageUploader(file);
        bannerPic.setImage(ImageLoader.getBannerImage(newBannerPic));
    }

    @FXML
    private void chooseProfilePic() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(profilePic.getScene().getWindow());

        newProfilePic = ImageLoader.profileImageUploader(file);
        profilePic.setImage(ImageLoader.getProfileImage(newProfilePic));
    }

    @FXML
    private void discard() {

        profilePic.setImage(ImageLoader.getProfileImage(CurrentClient.getUser().getProfilePic()));

        bannerPic.setImage(ImageLoader.getBannerImage(CurrentClient.getUser().getBannerPic()));

        newDisplayName.setText(CurrentClient.getUser().getDisplayName());
        newBio.setText(CurrentClient.getUser().getBio());

        newBannerPic = CurrentClient.getUser().getBannerPic();
        newProfilePic = CurrentClient.getUser().getProfilePic();

        overlay2.setVisible(false);
        overlay2.setManaged(false);
        discardWindow.setVisible(false);
        discardWindow.setManaged(false);
        overlay1.setVisible(false);
        overlay1.setManaged(false);
        editWindow.setVisible(false);
        editWindow.setManaged(false);
    }

    @FXML
    private void cancelDiscard() {
        overlay2.setVisible(false);
        overlay2.setManaged(false);
        discardWindow.setVisible(false);
        discardWindow.setManaged(false);
    }

    //____________________________________________________________
    //post or reply window

    @FXML
    private void closePostWindow() {
        images.clear();
        postText.clear();
        overlay3.setVisible(false);
        overlay3.setManaged(false);
        postWindow.setVisible(false);
        postWindow.setManaged(false);
    }

    @FXML
    private void Post() {
        posting(images, postText.getText());
    }

    @FXML
    private void Media() {
        if (images.size() < 4) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(miniProfilePic.getScene().getWindow());

            String newImage = ImageLoader.tweetImageUploader(file);
            images.add(newImage);

            List<Button> btns = displayMedia(images, mediaBox);
            buttonStyling(btns);
        }
    }

    public void posting(List<String> imageList, String content) {

        ObjectNode payload = ServerConnection.mapper.createObjectNode();
        ObjectNode newTweet = ServerConnection.mapper.createObjectNode();

        payload.put("userId", CurrentClient.getUser().getId());
        payload.put("content", content);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        payload.put("timestamp", ts.toString());

        String[] imageArray = imageList.toArray(new String[0]);
        JsonNode mediaUrls = ServerConnection.mapper.valueToTree(imageArray);
        payload.set("mediaUrls", mediaUrls);

        if (isReplying) {
            payload.put("replyToTweetId", R_tweet.getId());
            newTweet.put("type", "CREATE_REPLY");
        }
        else {
            newTweet.put("type", "CREATE_TWEET");
        }

        newTweet.set("payload", payload);
        CurrentClient.getConnection().send(newTweet.toString());

        images.clear();
        postText.clear();
    }

    //____________________________________________________________
    //media display

    public void buttonStyling(List<Button> btns) {
        for (Button b : btns) {
            FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);
            deleteIcon.getStyleClass().add("half-transparent-icon");
            b.setGraphic(deleteIcon);
            b.setPrefHeight(24);
            b.setPrefWidth(24);
            b.setMaxHeight(24);
            b.setMaxWidth(24);
        }
    }

    private void deleteMedia(List<String> imageList, int index) {
        imageList.remove(index);
        List<Button> btns = displayMedia(imageList, mediaBox);
        buttonStyling(btns);
    }

    private void displayFullSizeImage(Image i) {
        fullsizeImage.setImage(i);
        overlay6.setManaged(true);
        overlay6.setVisible(true);
        fullsizeImage.setManaged(true);
        fullsizeImage.setVisible(true);
    }

    public List<Button> displayMedia(List<String> imageList, HBox mBox) {
        List<Button> BTNs = new ArrayList<>();

        mBox.getChildren().clear();
        mBox.setVisible(true);
        mBox.setManaged(true);

        switch (imageList.size()) {
            case 0: {
                mBox.setVisible(false);
                mBox.setManaged(false);
                break;
            }
            case 1: {
                ImageView iv = new ImageView(ImageLoader.getTweetImage(imageList.get(0)));
                iv.setOnMouseClicked(mouseEvent -> {
                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(0)));
                });
                iv.setFitWidth(500);
                iv.setFitHeight(300);

                iv.setPreserveRatio(true);
                Rectangle ra = new Rectangle();
                ra.setWidth(500);
                ra.setHeight(300);
                ra.setStyle("-fx-border-radius: 10;");
                iv.setClip(ra);
                Image image = iv.getImage();
                double x = (image.getWidth() - 500) / 2;
                double y = (image.getHeight() - 300) / 2;
                iv.setViewport(new Rectangle2D(x, y, 500, 300));

                StackPane sp = new StackPane();
                sp.setPrefWidth(500);
                sp.setPrefHeight(300);
                Button btn = new Button();
                btn.setOnAction(actionEvent -> deleteMedia(imageList,0));
                sp.getChildren().add(iv);
                sp.getChildren().add(btn);
                mBox.getChildren().add(sp);
                BTNs.add(btn);
                break;
            }
            case 2: {
                for (int i = 0; i <= 1; i++) {
                    ImageView iv = new ImageView(ImageLoader.getTweetImage(imageList.get(i)));
                    iv.setFitWidth(247);
                    iv.setFitHeight(300);

                    iv.setPreserveRatio(true);
                    Rectangle ra = new Rectangle();
                    ra.setWidth(247);
                    ra.setHeight(300);
                    ra.setStyle("-fx-border-radius: 10;");
                    iv.setClip(ra);
                    Image image = iv.getImage();
                    double x = (image.getWidth() - 247) / 2;
                    double y = (image.getHeight() - 300) / 2;
                    iv.setViewport(new Rectangle2D(x, y, 247, 300));

                    StackPane sp = new StackPane();
                    sp.setPrefWidth(247);
                    sp.setPrefHeight(300);
                    Button btn = new Button();
                    switch (i) {
                        case 0:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                            iv.setOnMouseClicked(mouseEvent -> {
                                displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(0)));
                            });
                            break;
                        }
                        case 1:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
                            iv.setOnMouseClicked(mouseEvent -> {
                                displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(1)));
                            });
                            break;
                        }
                        default:
                            break;
                    }
                    sp.getChildren().add(iv);
                    sp.getChildren().add(btn);
                    mBox.getChildren().add(sp);
                    BTNs.add(btn);
                }

                break;
            }
            case 3: {
                ImageView iv1 = new ImageView(ImageLoader.getTweetImage(imageList.get(0)));
                iv1.setOnMouseClicked(mouseEvent -> {
                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(0)));
                });
                iv1.setFitWidth(247);
                iv1.setFitHeight(300);

                iv1.setPreserveRatio(true);
                Rectangle ra1 = new Rectangle();
                ra1.setWidth(247);
                ra1.setHeight(300);
                ra1.setStyle("-fx-border-radius: 10;");
                iv1.setClip(ra1);
                Image image1 = iv1.getImage();
                double x1 = (image1.getWidth() - 247) / 2;
                double y1 = (image1.getHeight() - 300) / 2;
                iv1.setViewport(new Rectangle2D(x1, y1, 247, 300));

                StackPane sp1 = new StackPane();
                sp1.setPrefWidth(247);
                sp1.setPrefHeight(300);
                Button btn1 = new Button();
                btn1.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                sp1.getChildren().add(iv1);
                sp1.getChildren().add(btn1);
                mBox.getChildren().add(sp1);
                BTNs.add(btn1);

                VBox vb = new VBox();
                vb.setPrefWidth(247);
                vb.setPrefHeight(300);

                for (int i = 1; i <= 2; i++) {
                    ImageView iv = new ImageView(ImageLoader.getTweetImage(imageList.get(i)));
                    iv.setFitWidth(247);
                    iv.setFitHeight(147);

                    iv.setPreserveRatio(true);
                    Rectangle ra = new Rectangle();
                    ra.setWidth(247);
                    ra.setHeight(147);
                    ra.setStyle("-fx-border-radius: 10;");
                    iv.setClip(ra);
                    Image image = iv.getImage();
                    double x = (image.getWidth() - 247) / 2;
                    double y = (image.getHeight() - 147) / 2;
                    iv.setViewport(new Rectangle2D(x, y, 247, 147));

                    StackPane sp = new StackPane();
                    sp.setPrefWidth(247);
                    sp.setPrefHeight(147);
                    Button btn = new Button();
                    switch (i) {
                        case 1:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
                            iv1.setOnMouseClicked(mouseEvent -> {
                                displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(1)));
                            });
                            break;
                        }
                        case 2:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 2));
                            iv1.setOnMouseClicked(mouseEvent -> {
                                displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(2)));
                            });
                            break;
                        }
                        default:
                            break;
                    }
                    sp.getChildren().add(iv);
                    sp.getChildren().add(btn);
                    vb.getChildren().add(sp);
                    BTNs.add(btn);
                }
                mBox.getChildren().add(vb);

                break;
            }
            case 4: {
                for (int j = 0; j < 2; j++) {
                    VBox vb = new VBox();
                    vb.setPrefWidth(247);
                    vb.setPrefHeight(300);
                    for (int i = 0; i < 2; i++) {
                        ImageView iv = new ImageView(ImageLoader.getTweetImage(imageList.get(2 * i + j)));
                        iv.setFitWidth(247);
                        iv.setFitHeight(147);

                        iv.setPreserveRatio(true);
                        Rectangle ra = new Rectangle();
                        ra.setWidth(247);
                        ra.setHeight(147);
                        ra.setStyle("-fx-border-radius: 10;");
                        iv.setClip(ra);
                        Image image = iv.getImage();
                        double x = (image.getWidth() - 247) / 2;
                        double y = (image.getHeight() - 147) / 2;
                        iv.setViewport(new Rectangle2D(x, y, 247, 147));

                        StackPane sp = new StackPane();
                        sp.setPrefWidth(247);
                        sp.setPrefHeight(147);
                        Button btn = new Button();
                        switch (2 * i + j) {
                            case 0:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                                iv.setOnMouseClicked(mouseEvent -> {
                                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(0)));
                                });
                                break;
                            }
                            case 1:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
                                iv.setOnMouseClicked(mouseEvent -> {
                                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(1)));
                                });
                                break;
                            }
                            case 2:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 2));
                                iv.setOnMouseClicked(mouseEvent -> {
                                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(2)));
                                });
                                break;
                            }
                            case 3:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 3));
                                iv.setOnMouseClicked(mouseEvent -> {
                                    displayFullSizeImage(ImageLoader.getTweetImage(imageList.get(3)));
                                });
                                break;
                            }
                            default:
                                break;
                        }
                        sp.getChildren().add(iv);
                        sp.getChildren().add(btn);
                        vb.getChildren().add(sp);
                        BTNs.add(btn);
                    }
                    mBox.getChildren().add(vb);
                }
                break;
            }
            default:
                break;
        }

        return BTNs;
    }

    @FXML
    private void closeFullsizeImage() {
        overlay6.setManaged(false);
        overlay6.setVisible(false);
        fullsizeImage.setManaged(false);
        fullsizeImage.setVisible(false);
    }

    //____________________________________________________________
    //delete tweet window

    @FXML
    private void delete() {

        ObjectNode payload = ServerConnection.mapper.createObjectNode();
        ObjectNode DTweet = ServerConnection.mapper.createObjectNode();

        payload.put("tweetId", deletingTweet.getId());
        payload.put("userId", deletingTweet.getUserId());

        DTweet.put("type", "DELETE_TWEET");
        DTweet.set("payload", payload);

        CurrentClient.getConnection().send(DTweet.toString());

        deletingTweet = null;

        overlay4.setVisible(false);
        overlay4.setManaged(false);
        deleteWindow.setVisible(false);
        deleteWindow.setManaged(false);
    }

    @FXML
    private void cancelDelete() {

        deletingTweet = null;

        overlay4.setVisible(false);
        overlay4.setManaged(false);
        deleteWindow.setVisible(false);
        deleteWindow.setManaged(false);
    }

    public void displayDeleteWindow(Tweet t) {

        deletingTweet = t;

        overlay4.setManaged(true);
        overlay4.setVisible(true);
        deleteWindow.setManaged(true);
        deleteWindow.setVisible(true);
    }

    //____________________________________________________________
    //logout window

    @FXML
    private void logout() {

        //send the logout message
        ObjectNode payload = ServerConnection.mapper.createObjectNode();
        ObjectNode logout = ServerConnection.mapper.createObjectNode();

        payload.put("userId", CurrentClient.getUser().getId());

        logout.put("type", "LOGOUT");
        logout.set("payload", payload);

        CurrentClient.getConnection().send(logout.toString());

        CurrentClient.setUser(null);
        CurrentClient.getConnection().disconnect();
        CurrentClient.setConnection(null);

        //back to authentication
        try {
            Stage stage = (Stage) postBTN.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/fxmls/authentication.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 500, 500);
            newScene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());

            stage.setScene(newScene);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void cancelLogout() {
        overlay5.setManaged(false);
        overlay5.setVisible(false);
        logoutWindow.setManaged(false);
        logoutWindow.setVisible(false);
    }
}
