package server.services;

import server.database.daos.UserDao;
import shared.models.User;
import shared.utils.PasswordUtil;

import java.util.Map;

public class AuthService {

    private final UserDao userDAO = new UserDao();

    public User getCurrentUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userDAO.getUserByUsername(username);
    }

    public Map<String, Object> login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        if (!userDAO.checkPassword(user.getId(), password)) {
            return null;
        }

        user.setPasswordHash(null);

        return Map.of(
                "user", user
        );
    }

    public boolean register(String username, String email, String password, String displayName) {
        if (username == null || username.length() < 3) return false;
        if (email == null || email.isBlank()) return false;
        if (password == null || password.length() < 6) return false;

        if (userDAO.getUserByUsername(username) != null) return false;
        if (userDAO.getUserByEmail(email) != null) return false;

        String hashedPassword = PasswordUtil.hashPassword(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setDisplayName(displayName != null ? displayName : username);
        newUser.setBio("");
        newUser.setActive(true);
        newUser.setVerified(false);

        return userDAO.createUser(newUser);
    }

    public boolean logout(String username) {
        return true;
    }
}