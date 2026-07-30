package server;
import server.handlers.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ServerLauncher {
    private static final int PORT = 8081;
    public static void main(String[] args) {
        System.out.println("...Twitter Clone Server Starting...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            System.out.println("Waiting for clients...\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}