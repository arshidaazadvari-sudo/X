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
    public boolean logout(String token) {
        return authService.logout(token);
    }
    public User getCurrentUser(String token) {
        return authService.getCurrentUser(token);
    }
}
