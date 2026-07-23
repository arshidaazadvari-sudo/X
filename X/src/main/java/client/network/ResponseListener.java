package client.network;

import tools.jackson.databind.JsonNode;

import java.io.BufferedReader;

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
                switch (json.get("type").asText()) {
                    case "auth_error": {  //?
                        authError = json;
                        break;
                    }
                    case "tweet": {
                        //????????
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
