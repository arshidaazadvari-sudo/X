package client.network;

import java.io.ObjectInputStream;

public class ResponseListener implements Runnable {

    private final ObjectInputStream in;

    public ResponseListener(ObjectInputStream inputStream) { this.in = inputStream; }

    @Override
    public void run() {
        try {
            Object obj;
            while ((obj = in.readObject()) != null) {
                // deciding what to do with the received message from server based on its type
            }
        }
        catch (Exception e) {
            //
        }
    }
}
