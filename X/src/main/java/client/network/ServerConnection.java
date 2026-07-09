package client.network;

import client.ClientConfig;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected;

    public boolean connect() {
        try (Socket socket_ = new Socket(ClientConfig.HOST, ClientConfig.PORT)) {
            socket = socket_;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            isConnected = true;

            ResponseListener RL = new ResponseListener(in);
            Thread listener = new Thread(RL);
            listener.start();

            return isConnected;

        }
        catch (Exception e) {
            //
        }
        return false;
    }

    public void disconnect() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                isConnected = false;
            }
        }
        catch (Exception e) {
            //
        }

    }

    public void send(Object request) {
        try {
            out.writeObject(request);
            out.flush();
        }
        catch (Exception e) {
            //
        }
    }

    public boolean isConnected() { return isConnected; }

}
