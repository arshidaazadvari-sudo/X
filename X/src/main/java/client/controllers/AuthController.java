package client.controllers;

import client.network.ResponseListener;
import client.network.ServerConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import shared.models.User;
import shared.protocol.Notification;
import shared.protocol.NotificationType;

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
        boolean b = connection.connect();
        if (!b) {
            System.out.println("Connection failed.");
            //
        }

        emailText.setVisible(false);
        emailField.setVisible(false);
        error.setVisible(false);

        isLogin = true;
    }
    
    @FXML
    public void authentication() {
        User user = new User(usernameField.getText(), emailField.getText(), passwordField.getText());
        if (isLogin) {
            user.setEmail(null);
        }

        connection.send(user);

        if (ResponseListener.authNotif.getType().equals(NotificationType.AUTH_SUCCESS)) {
            //go to homepage
            //
        } else {
            //show the error to the user
            error.setText(ResponseListener.authNotif.getDetails());
            error.setVisible(true);
        }
    }
    
    @FXML
    public void switchPage() {

        ResponseListener.authNotif = null;
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
