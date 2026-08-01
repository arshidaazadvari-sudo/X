package client.network;

import client.controllers.HomeController;
import client.CurrentClient;
import shared.models.Tweet;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class ResponseListener implements Runnable {

    private final BufferedReader in;

    private static JsonNode authResponse;

    public ResponseListener(BufferedReader br) { this.in = br; }

    public static JsonNode getAuthResponse() { return authResponse; }

    public static void setAuthErrorNull() { authResponse = null; }

    @Override
    public void run() {
        JsonNode error = ServerConnection.mapper.readTree("{ \"type\": \"ERROR\" }");
        JsonNode success = ServerConnection.mapper.readTree("{ \"type\": \"SUCCESS\" }");
        JsonNode tweet = ServerConnection.mapper.readTree("{ \"type\": \"TWEET\" }");
        try {
            String message;
            while ((message = in.readLine()) != null) {
                JsonNode json = ServerConnection.mapper.readTree(message);

                // deciding what to do with the received message from server based on its type
                if (Objects.equals(json.get("type").toString(), success.get("type").toString())) {
                    authResponse = json;
                    System.out.println("A success response was received. ");
                }
                else if (Objects.equals(json.get("type").toString(), error.get("type").toString())) {
                    if (CurrentClient.getUser() == null) {
                        authResponse = json;
                        System.out.println("An auth error was received. ");
                    }
                    else {
                        System.out.println("An error was received from server: " + json.get("message").toString());
                    }
                }
                else if (Objects.equals(json.get("type").toString(), tweet.get("type").toString())) {
                    if (CurrentClient.isOnHomePage()) {

                        JsonNode payload = ServerConnection.mapper.readTree(json.get("payload").toString());

                        Tweet newT = new Tweet();

                        newT.setId(payload.get("id").asInt());
                        newT.setUserId(payload.get("userId").asInt());
                        newT.setContent(payload.get("content").toString());

                        String str = payload.get("timestamp").toString();
                        Timestamp ts = Timestamp.valueOf(str);
                        newT.setCreatedAt(ts);

                        newT.setLikesCount(payload.get("likesCount").asInt());
                        newT.setRetweetsCount(payload.get("retweetsCount").asInt());
                        newT.setRepliesCount(payload.get("repliesCount").asInt());
                        newT.setHashtags(ServerConnection.mapper.convertValue(payload.get("hashtags"), new TypeReference<List<String>>() {}));
                        newT.setMediaUrls(ServerConnection.mapper.convertValue(payload.get("mediaUrls"), String[].class));

                        boolean b1 = payload.get("isRetweet").asBoolean();
                        if (b1) newT.setRetweetOfTweetId(payload.get("originalTweetId").asInt());

                        boolean b2 = payload.get("isReply").asBoolean();
                        if (b2) newT.setReplyToTweetId(payload.get("replyToTweetId").asInt());

                        newT.setLikedByCurrentUser(payload.get("isLiked").asBoolean());
                        newT.setRetweetedByCurrentUser(payload.get("isRetweeted").asBoolean());

                        HomeController.addNewTweets(newT);
                    }
                }

            }
        }
        catch (Exception e) {
            //
        }
    }
}
