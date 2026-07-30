package server.services;

import server.database.daos.FollowDAO;
import server.database.daos.UserDao;
import shared.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowService {

    private final FollowDAO followDAO = new FollowDAO();
    private final UserDao userDAO = new UserDao();

    public Map<String, Object> follow(int currentUserId, String targetUsername) {
        Map<String, Object> result = new HashMap<>();

        if (targetUsername == null || targetUsername.isBlank()) {
            result.put("success", false);
            result.put("message", "Username is required");
            return result;
        }

        User targetUser = userDAO.getUserByUsername(targetUsername);
        if (targetUser == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        if (currentUserId == targetUser.getId()) {
            result.put("success", false);
            result.put("message", "You cannot follow yourself");
            return result;
        }

        boolean success = followDAO.follow(currentUserId, targetUser.getId());

        if (success) {
            result.put("success", true);
            result.put("message", "Successfully followed @" + targetUsername);
        } else {
            result.put("success", false);
            result.put("message", "Already following this user");
        }

        return result;
    }
    public Map<String, Object> unfollow(int currentUserId, String targetUsername) {
        Map<String, Object> result = new HashMap<>();

        if (targetUsername == null || targetUsername.isBlank()) {
            result.put("success", false);
            result.put("message", "Username is required");
            return result;
        }

        User targetUser = userDAO.getUserByUsername(targetUsername);
        if (targetUser == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        boolean success = followDAO.unfollow(currentUserId, targetUser.getId());

        if (success) {
            result.put("success", true);
            result.put("message", "Successfully unfollowed @" + targetUsername);
        } else {
            result.put("success", false);
            result.put("message", "You are not following this user");
        }

        return result;
    }

    public Map<String, Object> getFollowers(String username) {
        Map<String, Object> result = new HashMap<>();

        if (username == null || username.isBlank()) {
            result.put("success", false);
            result.put("message", "Username is required");
            return result;
        }

        User targetUser = userDAO.getUserByUsername(username);
        if (targetUser == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        List<User> followers = followDAO.getFollowers(targetUser.getId());
        int count = followDAO.getFollowerCount(targetUser.getId());

        result.put("success", true);
        result.put("users", followers);
        result.put("count", count);

        return result;
    }
    public Map<String, Object> getFollowing(String username) {
        Map<String, Object> result = new HashMap<>();

        if (username == null || username.isBlank()) {
            result.put("success", false);
            result.put("message", "Username is required");
            return result;
        }

        User targetUser = userDAO.getUserByUsername(username);
        if (targetUser == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return result;
        }

        List<User> following = followDAO.getFollowing(targetUser.getId());
        int count = followDAO.getFollowingCount(targetUser.getId());

        result.put("success", true);
        result.put("users", following);
        result.put("count", count);

        return result;
    }
}