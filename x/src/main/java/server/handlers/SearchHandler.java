package server.handlers;

import tools.jackson.databind.ObjectMapper;
import server.database.daos.TweetDao;
import server.database.daos.UserDao;
import server.services.AuthService;
import shared.models.Tweet;
import shared.models.User;
import shared.protocol.Request;
import shared.protocol.Response;

import java.util.List;
import java.util.Map;

public class SearchHandler {

    private final UserDao userDAO = new UserDao();
    private final TweetDao tweetDAO = new TweetDao();
    private final AuthService authService = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response handle(Request request) {
        return switch (request.getType()) {
            case "SEARCH_USERS"  -> searchUsers(request);
            case "SEARCH_TWEETS" -> searchTweets(request);
            default -> Response.error(request.getRequestId(), 400, "Unknown search type");
        };
    }
    private Response searchUsers(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String query = (String) payload.get("query");
            int limit = payload.get("limit") != null ? ((Number) payload.get("limit")).intValue() : 20;

            if (query == null || query.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Search query is required");
            }

            List<User> users = userDAO.searchUser(query);

            // Remove sensitive data
            users.forEach(u -> u.setPasswordHash(null));

            return Response.success("SEARCH_USERS", request.getRequestId(), Map.of(
                    "users", users,
                    "count", users.size()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Search users failed");
        }
    }

    private Response searchTweets(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String query = (String) payload.get("query");
            int limit = payload.get("limit") != null ? ((Number) payload.get("limit")).intValue() : 20;

            if (query == null || query.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Search query is required");
            }

            List<Tweet> tweets = tweetDAO.findTweetsByKeyword(query, limit);

            return Response.success("SEARCH_TWEETS", request.getRequestId(), Map.of(
                    "tweets", tweets,
                    "count", tweets.size()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Search tweets failed");
        }
    }
}
