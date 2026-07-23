package client.controllers;

import shared.models.User;

public class HomeController {

    private MainController mainController;

    private User user;

    public void setMainController(MainController mc) { this.mainController = mc; }

    public void setUser(User u) { this.user = u; }
}
