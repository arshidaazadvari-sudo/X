package client.network;

import client.session.ClientSession;
import shared.models.Tweet;
import tools.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.lang.classfile.Label;
import java.lang.reflect.Array;
import java.util.List;

public class ResponseListener implements Runnable {

    private final BufferedReader in;

    private static JsonNode authError;

    public ResponseListener(BufferedReader br) { this.in = br; }

    public static JsonNode getAuthError() { return authError; }

    public static void setAuthErrorNull() { authError = null; }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                JsonNode json = ServerConnection.mapper.readTree(message);

                // deciding what to do with the received message from server based on its type
                switch (json.get("type").toString()) {
                    case "auth_error": {  //???????
                        authError = json;
                        break;
                    }
                    case "tweet": {
                        if (ClientSession.isOnHomePage()) {
                            //?????????????
                            JsonNode payload = ServerConnection.mapper.readTree(json.get("payload").toString());
                            Tweet newT = new Tweet();
                            newT.setId(Integer.parseInt(payload.get("id").toString()));
                            newT.setUserId(Integer.parseInt(payload.get("userId").toString()));
                            newT.setContent(payload.get("content").toString());
                            //newT.setCreatedAt(payload.get("timestamp").toString());
                            newT.setLikesCount(Integer.parseInt(payload.get("likesCount").toString()));
                            //newT.set(Integer.parseInt(payload.get("retweetsCount").toString()));
                            newT.setRepliesCount(Integer.parseInt(payload.get("repliesCount").toString()));
                            //newT.setHashtags(payload.get("hashtags")));
                            //newT.setMediaUrls(payload.get("mediaUrls"));
                            //newT.set(Boolean.parseBoolean(payload.get("isRetweet").toString()));
                            newT.setRetweetOfTweetId(Integer.parseInt(payload.get("originalTweetId").toString()));
                            //newT.set(Boolean.parseBoolean(payload.get("isLiked").toString()));
                            //newT.set(Boolean.parseBoolean(payload.get("isRetweeted").toString()));
                            ClientSession.realTimeTweets.put(newT);
                        }
                        break;
                    }
                    default:
                        break;
                }

            }
        }
        catch (Exception e) {
            //
        }
    }
}
