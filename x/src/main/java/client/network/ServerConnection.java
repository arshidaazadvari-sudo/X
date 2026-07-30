package client.network;

import client.ClientConfig;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ServerConnection {

    public static ObjectMapper mapper = new ObjectMapper();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isConnected;

    public boolean connect() {
        try (Socket socket_ = new Socket(ClientConfig.HOST, ClientConfig.PORT)) {
            socket = socket_;

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            ResponseListener RL = new ResponseListener(in);
            Thread listener = new Thread(RL);
            listener.start();

            isConnected = true;

            return isConnected;

        }
        catch (Exception e) {
            System.out.println("Failure in connection. ");
            e.printStackTrace();
            System.out.println(e.getMessage());
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
            System.out.println("Failure in disconnection. ");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    public void send(String request) {
        try {
            out.println(request);
        }
        catch (Exception e) {
            System.out.println("Failure in sending the string of json. ");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public boolean isConnected() { return isConnected; }

}
