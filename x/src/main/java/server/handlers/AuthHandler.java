package server.handlers;
import server.services.AuthService;
import shared.models.User;
import java.util.Map;
public class AuthHandler {
    private final AuthService authService;
    public AuthHandler() {
        this.authService = new AuthService();
    }
    public Map<String, Object> login(String username, String password) {
        return authService.login(username, password);
    }
    public boolean register(String username, String email, String password, String displayName) {
        return authService.register(username, email, password, displayName);
    }
    public boolean logout(int userId) {
        return authService.logout(userId);
    }
    public User getCurrentUser(int userId) {
        return authService.getCurrentUser(userId);
    }
}
