package client.session;

import client.network.ServerConnection;
import shared.models.User;

public class Client {

    private static User user;

    private static boolean onHomePage;

    private static ServerConnection connection;

    public static void setConnection(ServerConnection c) { connection = c; }
    public static ServerConnection getConnection() { return connection; }

    public static void setUser(User u) { user = u; }
    public static User getUser() { return user; }

    public static void setOnHomePage(boolean b) { onHomePage = b; }
    public static boolean isOnHomePage() { return onHomePage; }
}
