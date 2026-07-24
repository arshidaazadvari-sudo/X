package client.session;

import client.controllers.UserCardController;
import shared.models.Tweet;
import shared.models.User;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSession {

    private static User user;
    private static SessionState state;

    private static boolean onHomePage;

    public static void setUser(User u) { user = u; }
    public static User getUser() { return user; }

    public void setState(SessionState s) { state = s; }
    public static SessionState getState() { return state; }

    public static void setOnHomePage(boolean b) { onHomePage = b; }
    public static boolean isOnHomePage() { return onHomePage; }
}
