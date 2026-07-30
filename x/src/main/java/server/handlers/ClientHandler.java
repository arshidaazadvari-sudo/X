package server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import shared.protocol.Request;
import shared.protocol.Response;
import server.services.AuthService;
import server.handlers.FollowHandler;   // and later TweetHandler, LikeHandler...

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService = new AuthService();

    // Handlers
    private final FollowHandler followHandler = new FollowHandler();
    // private final TweetHandler tweetHandler = new TweetHandler();
    // private final LikeHandler likeHandler = new LikeHandler();
    // private final AuthHandler authHandler = new AuthHandler();

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
        String type = request.getType();

        return switch (type) {
            // Auth
            case "REGISTER", "LOGIN", "LOGOUT", "AUTH", "ME" -> {
                // return authHandler.handle(request);
                yield Response.error(request.getRequestId(), 501, "Auth handler not implemented yet");
            }

            // Follow
            case "FOLLOW", "UNFOLLOW", "GET_FOLLOWERS", "GET_FOLLOWING" ->
                    followHandler.handle(request);

            // Tweet (later)
            case "CREATE_TWEET", "DELETE_TWEET", "GET_HOME_TIMELINE",
                 "GET_USER_TIMELINE", "SEARCH_TWEETS" -> {
                // return tweetHandler.handle(request);
                yield Response.error(request.getRequestId(), 501, "Tweet handler not implemented yet");
            }

            // Like (later)
            case "LIKE_TWEET", "UNLIKE_TWEET" -> {
                // return likeHandler.handle(request);
                yield Response.error(request.getRequestId(), 501, "Like handler not implemented yet");
            }

            default -> Response.error(request.getRequestId(), 400, "Unknown request type: " + type);
        };
    }
}