package client.controllers;

import client.ClientApp;
import client.network.ResponseListener;
import client.network.ServerConnection;
import client.session.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class AuthController {

    private ServerConnection connection;

    private boolean isLogin;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    @FXML private Button AuthBTN;
    @FXML private Label switchPageText;
    @FXML private Button switchPageBTN;

    @FXML private Label emailText;
    @FXML private TextField emailField;
    @FXML private TextArea error;

    @FXML
    public void initialize() {

        ServerConnection c = new ServerConnection();
        Client.setConnection(c);
        connection = c;

        boolean b = connection.connect();
        if (!b) {
            System.out.println("Connection failed.");
            //?????????????????????
        }

        emailText.setVisible(false);
        emailField.setVisible(false);
        error.setVisible(false);

        isLogin = true;
    }
    
    @FXML
    public void authentication() {

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

        //wait 500 milliseconds while server & response listener do their part (set an auth error if there's any)
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            //
        }

        if (ResponseListener.getAuthError() == null) {
            //Client.setUser(); ???????????????????

            //go to main scene
            try {
                Stage stage = (Stage) AuthBTN.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("main.fxml"));
                Scene newScene = new Scene(fxmlLoader.load(), 700, 700); //style ????????????????????????????
                newScene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

                stage.setScene(newScene);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //show the error to the user
            error.setText(ResponseListener.getAuthError().get("error message").toString()); //????????????
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
            switchPageBTN.setText("New to X?");
            switchPageText.setText("Sign up");
        }
        else {
            AuthBTN.setText("Sign up");
            switchPageBTN.setText("Already have an account?");
            switchPageText.setText("Log in");
        }

    }
}
