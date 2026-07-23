package client.session;

import client.controllers.UserCardController;
import shared.models.User;

public class ClientSession {

    private static User user;
    private static SessionState state;

    public void setUser(User u) { user = u; }
    public static User getUser() { return user; }

    public void setState(SessionState s) { state = s; }
    public static SessionState getState() { return state; }
}
