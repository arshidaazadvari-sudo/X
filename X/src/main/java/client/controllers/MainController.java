package client.controllers;

import client.network.ServerConnection;
import client.session.ClientSession;
import client.utils.DateFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import server.database.daos.UserDao;
import shared.models.Tweet;
import shared.models.User;
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
    @FXML private TextArea R_tweetText;

    @FXML private Button home;
    @FXML private Button explore;
    @FXML private Button profile;
    @FXML private Button post;
    @FXML private Button account;

    @FXML private Pane overlay1;
    @FXML private AnchorPane editWindow;
    @FXML private Pane overlay2;
    @FXML private VBox discardWindow;
    @FXML private Pane overlay3;
    @FXML private VBox postWindow;

    @FXML private ImageView bannerPic;
    @FXML private ImageView profilePic;
    @FXML private TextField newDisplayName;
    @FXML private TextArea newBio;
    @FXML private Label blankNameMSG;

    @FXML private ImageView miniProfilePic;
    @FXML private TextArea postText;
    @FXML private HBox mediaBox;
    @FXML private Button mediaBTN;
    private List<Image> images;

    @FXML private VBox container;

    private final String defaultProfile = "/images/default_profile.png";
    private final String defaultBanner = "/images/default_banner.png";

    private String newProfilePic;
    private String newBannerPic;

    private boolean isReplying;
    private Tweet R_tweet;

    public void setIsReplying(boolean b) { isReplying = b; }
    public void setRTweet(Tweet rt) { R_tweet = rt; }

    public VBox getContainer() { return container; }

    @FXML
    private void initialize() {

        //initial values

        String profile_addresss;
        if (ClientSession.getUser().getProfilePic() != null) profile_addresss = ClientSession.getUser().getProfilePic();
        else profile_addresss = defaultProfile;
        Image profile_pic = new Image(getClass().getResourceAsStream(profile_addresss));
        profilePic.setImage(profile_pic);

        String banner_address;
        if (ClientSession.getUser().getBannerPic() != null) banner_address = ClientSession.getUser().getBannerPic();
        else banner_address = defaultBanner;
        Image banner_pic = new Image(getClass().getResourceAsStream(banner_address));
        bannerPic.setImage(banner_pic);

        String miniPro_address;
        if (ClientSession.getUser().getProfilePic() != null) miniPro_address = ClientSession.getUser().getProfilePic();
        else miniPro_address = defaultProfile;
        Image miniPro_pic = new Image(getClass().getResourceAsStream(miniPro_address));
        miniProfilePic.setImage(miniPro_pic);

        newDisplayName.setText(ClientSession.getUser().getDisplayName());
        newBio.setText(ClientSession.getUser().getBio());


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
        overlay1.setVisible(false);
        overlay1.setManaged(false);
        editWindow.setVisible(false);
        editWindow.setManaged(false);
        overlay2.setVisible(false);
        overlay2.setManaged(false);
        discardWindow.setVisible(false);
        discardWindow.setManaged(false);
        overlay3.setVisible(false);
        overlay3.setManaged(false);
        postWindow.setVisible(false);
        postWindow.setManaged(false);
        replyingTo.setVisible(false);
        replyingTo.setManaged(false);

        isReplying = false;

        //the default landing page
        Home();
    }

    public void displayFollowersAndFollowings(User user, boolean onFollowers) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("followers&followings.fxml"));

            HBox fBox = loader.load();

            FollowController controller = loader.getController();
            controller.setOnFollowers(onFollowers);
            controller.setUser(user);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(fBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void anyProfile(User u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));

            HBox uBox = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(u);
            controller.setMainController(this);

            container.getChildren().clear();
            container.getChildren().add(uBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //____________________________________________________________
    //sidebar menu on the main scene

    @FXML
    private void Profile() {
        ClientSession.setOnHomePage(false);
        anyProfile(ClientSession.getUser());
    }

    @FXML
    private void Home() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));

            HBox hBox = loader.load();

            HomeController controller = loader.getController();
            controller.setMainController(this);
            controller.setUser(ClientSession.getUser());

            container.getChildren().clear();
            container.getChildren().add(hBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Explore() {
        ClientSession.setOnHomePage(false);
        //
    }

    @FXML
    public void PostWindow() {

        // the replying-to tweet (if any)
        if (isReplying) {
            replyingTo.setManaged(true);
            replyingTo.setVisible(true);
            postBTN.setText("Reply");

            //R_ProfilePic.setImage(); ????????????????????
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
    private void Account() {
        //
    }

    //____________________________________________________________
    //edit profile window

    public void setEditWindow() {
        overlay1.setVisible(true);
        editWindow.setVisible(true);
    }

    @FXML
    private void closeEditWindow() {
        if (ClientSession.getUser().getBannerPic().equals(newBannerPic) &&
                ClientSession.getUser().getProfilePic().equals(newProfilePic) &&
                ClientSession.getUser().getDisplayName().equals(newDisplayName.getText()) &&
                ClientSession.getUser().getBio().equals(newBio.getText())) {
            overlay1.setVisible(false);
            editWindow.setVisible(false);
        }
        else {
            overlay2.setVisible(true);
            discardWindow.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() {
        if (newDisplayName.getText() != null) {
            User updatedUser = new User(ClientSession.getUser().getUsername(),
                    ClientSession.getUser().getEmail(),
                    ClientSession.getUser().getPasswordHash());
            updatedUser.setId(ClientSession.getUser().getId());
            updatedUser.setProfilePic(newProfilePic);
            updatedUser.setBannerPic(newBannerPic);
            updatedUser.setDisplayName(newDisplayName.getText());
            updatedUser.setBio(newBio.getText());
            UserDao userDao = new UserDao();
            boolean b = userDao.updateUser(updatedUser);
            if (b) {
                User u = userDao.getUserById(ClientSession.getUser().getId());
                ClientSession.setUser(u);
            } else {
                System.out.println("Failed to save the changes. ");
            }

            overlay1.setVisible(false);
            editWindow.setVisible(false);
        }
        else {
            //change the style of the name box ??????????????????
            blankNameMSG.setVisible(true);
        }
    }

    @FXML
    private void chooseBannerPic() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(bannerPic.getScene().getWindow());

        if(file != null) {
            Image image = new Image(file.toURI().toString());
            bannerPic.setImage(image);
            //newBannerPic = ; ???????????
        }
    }

    @FXML
    private void chooseProfilePic() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG Images", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(profilePic.getScene().getWindow());

        if(file != null) {
            Image image = new Image(file.toURI().toString());
            profilePic.setImage(image);
            //newProfilePic = ; ???????????
        }
    }

    @FXML
    private void discard() {
        overlay2.setVisible(false);
        discardWindow.setVisible(false);
        overlay1.setVisible(false);
        editWindow.setVisible(false);
    }

    @FXML
    private void cancel() {
        overlay2.setVisible(false);
        discardWindow.setVisible(false);
    }

    //____________________________________________________________
    //post or reply

    @FXML
    private void closePostWindow() {
        images.clear();
        postText.clear();
        overlay3.setVisible(false);
        overlay3.setManaged(false);
        postWindow.setVisible(false);
        postWindow.setManaged(false);
        //set the style of mediaBTN back to default ????????????
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

            if (file != null) {
                Image image = new Image(file.toURI().toString());
                images.add(image);
            }

            if (images.size() >= 4) {
                //change the style of mediaBTN ????????????
            }

            List<Button> btns = displayMedia(images, mediaBox);
            buttonStyling(btns);
        }
    }

    private void deleteMedia(List<Image> imageList, int index) {
        imageList.remove(index);
        List<Button> btns = displayMedia(imageList, mediaBox);
        buttonStyling(btns);
    }

    public List<Button> displayMedia(List<Image> imageList, HBox mBox) {
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
                ImageView iv = new ImageView(imageList.get(0));
                iv.setFitWidth(500);
                iv.setFitHeight(300);
                StackPane sp = new StackPane();
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
                    ImageView iv = new ImageView(imageList.get(i));
                    iv.setFitWidth(247);
                    iv.setFitHeight(300);
                    StackPane sp = new StackPane();
                    Button btn = new Button();
                    switch (i) {
                        case 0:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                            break;
                        }
                        case 1:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
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
                ImageView iv1 = new ImageView(imageList.get(0));
                iv1.setFitWidth(247);
                iv1.setFitHeight(300);
                StackPane sp1 = new StackPane();
                Button btn1 = new Button();
                btn1.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                sp1.getChildren().add(iv1);
                sp1.getChildren().add(btn1);
                mBox.getChildren().add(sp1);
                BTNs.add(btn1);

                VBox vb = new VBox();

                for (int i = 1; i <= 2; i++) {
                    ImageView iv = new ImageView(imageList.get(i));
                    iv.setFitWidth(247);
                    iv.setFitHeight(147);
                    StackPane sp = new StackPane();
                    Button btn = new Button();
                    switch (i) {
                        case 1:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
                            break;
                        }
                        case 2:{
                            btn.setOnAction(actionEvent -> deleteMedia(imageList, 2));
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
                    for (int i = 0; i < 2; i++) {
                        ImageView iv = new ImageView(imageList.get(2 * i + j));
                        iv.setFitWidth(247);
                        iv.setFitHeight(147);
                        StackPane sp = new StackPane();
                        Button btn = new Button();
                        switch (2 * i + j) {
                            case 0:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 0));
                                break;
                            }
                            case 1:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 1));
                                break;
                            }
                            case 2:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 2));
                                break;
                            }
                            case 3:{
                                btn.setOnAction(actionEvent -> deleteMedia(imageList, 3));
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

    public void buttonStyling(List<Button> btns) {
        for (Button b : btns) {
            b.setText("close icon");
            //style the button ???????????????????
        }
    }

    public void posting(List<Image> imageList, String content) {
        if (!isReplying) {
            ObjectNode payload = ServerConnection.mapper.createObjectNode();
            ObjectNode newTweet = ServerConnection.mapper.createObjectNode();

            payload.put("userId", ClientSession.getUser().getId());
            payload.put("content", content);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            payload.put("timestamp", ts.toString());
            //????????????????
            //payload.put("mediaUrls", );

            newTweet.put("type", "CREATE_TWEET");
            newTweet.set("payload", payload);
            AuthController.getConnection().send(newTweet.toString());
        }
        else {
            //send the reply ????????????????
        }
    }


}
