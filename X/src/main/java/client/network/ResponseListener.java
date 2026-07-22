package client.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ObjectInputStream;

public class ResponseListener implements Runnable {

    private final ObjectInputStream in;

    public ResponseListener(ObjectInputStream inputStream) { this.in = inputStream; }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // deciding what to do with the received message from server based on its type
                JsonObject json = JsonParser.parseString(message).getAsJsonObject();

                String type = json.get("type").getAsString();
                //

            }
        }
        catch (Exception e) {
            //
        }
    }
}
