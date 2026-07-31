package client.controllers;

import client.ClientApp;
import client.network.ResponseListener;
import client.network.ServerConnection;
import client.CurrentClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import server.database.daos.UserDao;
import shared.models.User;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class AuthController {

    private ServerConnection connection;

    private boolean isLogin;

    @FXML private ImageView xLogo;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    @FXML private Button AuthBTN;
    @FXML private Label switchPageText;
    @FXML private Button switchPageBTN;

    @FXML private Label emailText;
    @FXML private TextField emailField;
    @FXML private Label error;

    @FXML
    public void initialize() {

        //set the logo of X
        xLogo.setImage(new Image(getClass().getResourceAsStream("/black_logo.png")));

        //making a connection

        ServerConnection c = new ServerConnection();
        CurrentClient.setConnection(c);
        connection = c;

        CurrentClient.setDarkTheme(false);

        boolean b = connection.connect();
        if (!b) {
            System.out.println("Connection failed.");
        }

        //default landing page at start: login page

        emailText.setVisible(false);
        emailField.setVisible(false);
        error.setVisible(false);

        isLogin = true;
    }
    
    @FXML
    public void authentication() {

        //send the auth request to server

        ObjectNode payload = ServerConnection.mapper.createObjectNode();
        ObjectNode auth = ServerConnection.mapper.createObjectNode();

        payload.put("username", usernameField.getText());
        if (isLogin) {
            auth.put("type", "LOGIN");
        }
        else {
            payload.put("email", emailField.getText());
            auth.put("type", "REGISTER");
        }
        payload.put("password", passwordField.getText());

        auth.set("payload", payload);
        connection.send(auth.toString());

        //wait 2000 milliseconds while server & response listener do their part (& set an auth error if there's any)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        if (ResponseListener.getAuthResponse().get("type").toString().equals("SUCCESS")) {

            //get the user
            UserDao userDao = new UserDao();
            User u = userDao.getUserByUsername(usernameField.getText());
            CurrentClient.setUser(u);

            //go to main scene
            try {
                Stage stage = (Stage) AuthBTN.getScene().getWindow();
                stage.setTitle("X");
                FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/fxmls/main.fxml"));
                Scene newScene = new Scene(fxmlLoader.load(), 850, 750);
                newScene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());

                stage.setScene(newScene);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        } else {
            //show the error to the user
            error.setText(ResponseListener.getAuthResponse().get("message").toString());
            error.setVisible(true);
        }
    }
    
    @FXML
    public void switchPage() {

        ResponseListener.setAuthErrorNull();
        error.setText(null);
        error.setVisible(false);

        emailText.setVisible(!emailText.isVisible());
        emailField.setVisible(!emailField.isVisible());

        isLogin = !isLogin;
        if (isLogin) {
            AuthBTN.setText("Log in");
            switchPageText.setText("New to X?");
            switchPageBTN.setText("Sign up");
        }
        else {
            AuthBTN.setText("Sign up");
            switchPageText.setText("Already have an account?");
            switchPageBTN.setText("Log in");
        }

    }
}
