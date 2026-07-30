package server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import server.database.daos.TweetDao;
import server.services.AuthService;
import shared.models.Tweet;
import shared.models.User;
import shared.protocol.Request;
import shared.protocol.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService = new AuthService();
    private final FollowHandler followHandler = new FollowHandler();
    private final SearchHandler searchHandler = new SearchHandler();
    private final TweetDao tweetDAO = new TweetDao();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    Request request = mapper.readValue(line, Request.class);
                    System.out.println("→ Received: " + request.getType());

                    Response response = routeRequest(request);
                    out.println(mapper.writeValueAsString(response));

                } catch (Exception e) {
                    e.printStackTrace();
                    Response error = Response.error(null, 500, "Internal server error");
                    out.println(mapper.writeValueAsString(error));
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private Response routeRequest(Request request) {
        if (request == null || request.getType() == null) {
            return Response.error(null, 400, "Invalid request");
        }

        return switch (request.getType().toUpperCase()) {

            // Auth
            case "REGISTER"     -> handleRegister(request);
            case "LOGIN"        -> handleLogin(request);
            case "LOGOUT"       -> handleLogout(request);
            case "ME", "AUTH"   -> handleMe(request);

            // Tweet
            case "CREATE_TWEET"     -> handleCreateTweet(request);
            case "DELETE_TWEET"     -> handleDeleteTweet(request);
            case "CREATE_REPLY"     -> handleCreateReply(request);
            case "CREATE_RETWEET"   -> handleCreateRetweet(request);
            case "GET_HOME_TIMELINE", "GET_USER_TIMELINE" ->
                    Response.error(request.getRequestId(), 501, "Timeline not implemented yet");

            // Follow
            case "FOLLOW", "UNFOLLOW", "GET_FOLLOWERS", "GET_FOLLOWING" ->
                    followHandler.handle(request);

            // Search
            case "SEARCH_USERS", "SEARCH_TWEETS" ->
                    searchHandler.handle(request);

            // Like
            case "LIKE_TWEET", "UNLIKE_TWEET" ->
                    Response.error(request.getRequestId(), 501, "Like not implemented yet");

            default -> Response.error(request.getRequestId(), 400,
                    "Unknown request type: " + request.getType());
        };
    }
    private Response handleRegister(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);

            String username    = (String) payload.get("username");
            String email       = (String) payload.get("email");
            String password    = (String) payload.get("password");
            String displayName = (String) payload.get("displayName");

            boolean success = authService.register(username, email, password, displayName);

            if (success) {
                return Response.success("REGISTER", request.getRequestId(),
                        Map.of("message", "User registered successfully"));
            } else {
                return Response.error(request.getRequestId(), 409,
                        "Username or email already exists / invalid data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Registration failed");
        }
    }

    private Response handleLogin(Request request) {
        try {
            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);

            String username = (String) payload.get("username");
            String password = (String) payload.get("password");

            Map<String, Object> result = authService.login(username, password);

            if (result != null) {
                return Response.success("LOGIN", request.getRequestId(), result);
            } else {
                return Response.error(request.getRequestId(), 401, "Invalid username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Login failed");
        }
    }

    private Response handleLogout(Request request) {
        boolean success = authService.logout(request.getToken());

        if (success) {
            return Response.success("LOGOUT", request.getRequestId(),
                    Map.of("message", "Logged out successfully"));
        } else {
            return Response.error(request.getRequestId(), 400, "Invalid or expired token");
        }
    }

    private Response handleMe(Request request) {
        User currentUser = authService.getCurrentUser(request.getToken());

        if (currentUser == null) {
            return Response.error(request.getRequestId(), 401, "You are not logged in");
        }

        currentUser.setPasswordHash(null);
        return Response.success("ME", request.getRequestId(), currentUser);
    }


    private Response handleCreateTweet(Request request) {
        try {
            User currentUser = authService.getCurrentUser(request.getToken());
            if (currentUser == null) {
                return Response.error(request.getRequestId(), 401, "You must be logged in");
            }

            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String content = (String) payload.get("content");

            if (content == null || content.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Tweet content cannot be empty");
            }

            Tweet tweet = new Tweet();
            tweet.setUserId(currentUser.getId());
            tweet.setContent(content.trim());

            boolean success = tweetDAO.createTweet(tweet);

            if (success) {
                return Response.success("CREATE_TWEET", request.getRequestId(), tweet);
            } else {
                return Response.error(request.getRequestId(), 500, "Failed to create tweet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Create tweet failed");
        }
    }

    private Response handleDeleteTweet(Request request) {
        try {
            User currentUser = authService.getCurrentUser(request.getToken());
            if (currentUser == null) {
                return Response.error(request.getRequestId(), 401, "You must be logged in");
            }

            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            int tweetId = ((Number) payload.get("tweetId")).intValue();

            boolean success = tweetDAO.deleteTweet(tweetId, currentUser.getId());

            if (success) {
                return Response.success("DELETE_TWEET", request.getRequestId(),
                        Map.of("message", "Tweet deleted successfully"));
            } else {
                return Response.error(request.getRequestId(), 403, "You can only delete your own tweets");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Delete tweet failed");
        }
    }

    private Response handleCreateReply(Request request) {
        try {
            User currentUser = authService.getCurrentUser(request.getToken());
            if (currentUser == null) {
                return Response.error(request.getRequestId(), 401, "You must be logged in");
            }

            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            String content = (String) payload.get("content");
            int replyToTweetId = ((Number) payload.get("replyToTweetId")).intValue();

            if (content == null || content.isBlank()) {
                return Response.error(request.getRequestId(), 400, "Reply content cannot be empty");
            }

            Tweet reply = new Tweet();
            reply.setUserId(currentUser.getId());
            reply.setContent(content.trim());
            reply.setReplyToTweetId(replyToTweetId);

            boolean success = tweetDAO.createTweet(reply);

            if (success) {
                return Response.success("CREATE_REPLY", request.getRequestId(), reply);
            } else {
                return Response.error(request.getRequestId(), 500, "Failed to create reply");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Create reply failed");
        }
    }

    private Response handleCreateRetweet(Request request) {
        try {
            User currentUser = authService.getCurrentUser(request.getToken());
            if (currentUser == null) {
                return Response.error(request.getRequestId(), 401, "You must be logged in");
            }

            Map<String, Object> payload = mapper.convertValue(request.getPayload(), Map.class);
            int originalTweetId = ((Number) payload.get("originalTweetId")).intValue();

            Tweet retweet = new Tweet();
            retweet.setUserId(currentUser.getId());
            retweet.setContent(""); // retweets usually have empty content
            retweet.setRetweetOfTweetId(originalTweetId);

            boolean success = tweetDAO.createTweet(retweet);

            if (success) {
                return Response.success("CREATE_RETWEET", request.getRequestId(), retweet);
            } else {
                return Response.error(request.getRequestId(), 500, "Failed to retweet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(request.getRequestId(), 500, "Retweet failed");
        }
    }
}