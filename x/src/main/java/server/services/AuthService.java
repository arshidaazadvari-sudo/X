package server.services;

import server.database.daos.UserDao;
import shared.models.User;
import shared.utils.PasswordUtil;

import java.util.Map;

public class AuthService {

    private final UserDao userDAO = new UserDao();

    public User getCurrentUser(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userDAO.getUserById(userId);
    }

    public Map<String, Object> login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user == null || !user.isActive()) {
            return null;
        }

        if (!userDAO.checkPassword(user.getId(), password)) {
            return null;
        }

        user.setPasswordHash(null);

        return Map.of(
                "userId", user.getId(),
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


    public boolean logout(int userId) {
        // Nothing to clear on server when using plain userId
        return userId > 0;
    }
}