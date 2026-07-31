package server.handlers;

import tools.jackson.databind.ObjectMapper;
import server.database.daos.FollowDAO;
import server.database.daos.UserDao;          // you need this to resolve username → id
import server.services.AuthService;          // to get current user from token
import shared.models.User;
import shared.protocol.Request;
import shared.protocol.Response;

import java.util.List;
import java.util.Map;

public class FollowHandler {
    private final FollowDAO followDAO = new FollowDAO();
    private final UserDao userDAO = new UserDao();
    private final AuthService authService = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();
    public Response handle(Request request) {
        String type = request.getType();
        User currentUser = authService.getCurrentUser(request.getToken());
        if (currentUser == null) {
            return Response.error(request.getRequestId(), 401, "You must be logged in");
        }

        return switch (type) {
            case "FOLLOW"          -> follow(request, currentUser);
            case "UNFOLLOW"        -> unfollow(request, currentUser);
            case "GET_FOLLOWERS"   -> getFollowers(request);
            case "GET_FOLLOWING"   -> getFollowing(request);
            default                -> Response.error(request.getRequestId(), 400, "Unknown follow action");
        };
    }
    private Response follow(Request request, User currentUser) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String targetUsername = (String) payload.get("username");

            if (targetUsername == null || targetUsername.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Username is required");
            }

            User targetUser = userDAO.getUserByUsername(targetUsername);
            if (targetUser == null) {
                return Response.error(request.getRequestId(), 404, "User not found");
            }

            boolean success = followDAO.follow(currentUser.getId(), targetUser.getId());

            if (success) {
                return Response.success("FOLLOW", request.getRequestId(),
                        Map.of("message", "Successfully followed @" + targetUsername));
            } else {
                return Response.error(request.getRequestId(), 400, "Could not follow user (already following or self-follow)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Internal server error");
        }
    }
    private Response unfollow(Request request, User currentUser) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String targetUsername = (String) payload.get("username");

            if (targetUsername == null || targetUsername.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Username is required");
            }

            User targetUser = userDAO.getUserByUsername(targetUsername);
            if (targetUser == null) {
                return Response.error(request.getRequestId(), 404, "User not found");
            }

            boolean success = followDAO.unfollow(currentUser.getId(), targetUser.getId());

            if (success) {
                return Response.success("UNFOLLOW", request.getRequestId(),
                        Map.of("message", "Successfully unfollowed @" + targetUsername));
            } else {
                return Response.error(request.getRequestId(), 400, "You are not following this user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Internal server error");
        }
    }

    // ==================== GET FOLLOWERS ====================
    private Response getFollowers(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String username = (String) payload.get("username");

            if (username == null || username.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Username is required");
            }

            User targetUser = userDAO.getUserByUsername(username);
            if (targetUser == null) {
                return Response.error(request.getRequestId(), 404, "User not found");
            }

            List<User> followers = followDAO.getFollowers(targetUser.getId());
            int count = followDAO.getFollowerCount(targetUser.getId());

            return Response.success("GET_FOLLOWERS", request.getRequestId(), Map.of(
                    "users", followers,
                    "count", count
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Internal server error");
        }
    }
    private Response getFollowing(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String username = (String) payload.get("username");

            if (username == null || username.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Username is required");
            }

            User targetUser = userDAO.getUserByUsername(username);
            if (targetUser == null) {
                return Response.error(request.getRequestId(), 404, "User not found");
            }

            List<User> following = followDAO.getFollowing(targetUser.getId());
            int count = followDAO.getFollowingCount(targetUser.getId());

            return Response.success("GET_FOLLOWING", request.getRequestId(), Map.of(
                    "users", following,
                    "count", count
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Internal server error");
        }
    }
}