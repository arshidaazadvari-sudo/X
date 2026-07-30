package server.services;
import server.database.daos.UserDao;
import shared.models.User;
import shared.utils.PasswordUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final UserDao userDAO = new UserDao();
    private final ConcurrentHashMap<String, Integer> sessions = new ConcurrentHashMap<>();
    public User getCurrentUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Integer userId = sessions.get(token);
        if (userId == null) {
            return null;
        }
        return userDAO.getUserById(userId);
    }
    public Map<String, Object> login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return null; // user not found
        }
        if (!userDAO.checkPassword(user.getId(), password)) {
            return null; // wrong password
        }
        String token = generateToken();
        sessions.put(token, user.getId());
        return Map.of(
                "token", token,
                "user", user
        );
    }
    public boolean register(String username, String email, String password, String displayName) {
        if (username == null || username.length() < 3) return false;
        if (email == null || email.isBlank()) return false;
        if (password == null || password.length() < 6) return false;
        if (userDAO.getUserByUsername(username) != null) {
            return false;
        }
        if (userDAO.getUserByEmail(email) != null) {
            return false;
        }
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
    public boolean logout(String token) {
        if (token == null) return false;
        return sessions.remove(token) != null;
    }
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}