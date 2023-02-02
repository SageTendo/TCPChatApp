package server;

import utils.Logger;
import utils.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private final String IP_ADDRESS;
    private final int PORT;
    static final int REQUIRED_USERNAME_LENGTH = 2;
    private final ServerSocket serverSocket;
    static final HashMap<String, ServerThread> connectedClients = new HashMap<>();
    static final ArrayList<String> clientUsernames = new ArrayList<>();


    public Server(String ip, int port) throws IOException {
        this.IP_ADDRESS = ip;
        this.PORT = port;

        this.serverSocket = new ServerSocket(PORT);
        newClientConnectionListener();
        Logger.toConsole("SERVER", String.format("Server Started on port %d", this.PORT));
    }

    /**
     * Listens for new client socket connections.
     */
    private void newClientConnectionListener() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = this.serverSocket.accept();
                    String log = String.format("New socket {%s:%s} connection...",
                            clientSocket.getInetAddress(), clientSocket.getInetAddress()
                    );
                    Logger.toConsole("CONNECTION", log);

                    ServerThread newClient = new ServerThread(clientSocket);
                    newClient.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    /**
     * Sends a message to all connected clients, in a separate thread
     *
     * @param message The message to be broadcast to all connected clients
     */
    static synchronized void broadcastMessage(Message message) {
        new Thread(() -> {
            for (ServerThread client : connectedClients.values()) {
                client.sendMessage(message);
            }
        }).start();
    }


    /**
     * Add the new client handler to the list of connected clients and their username to the list of client usernames
     *
     * @param client   The thread that handles the connected client.
     * @param username The username that the client provided.
     */
    static synchronized void addClient(ServerThread client, String username) {
        clientUsernames.add(username);
        connectedClients.put(username, client);
    }

    /**
     * FIXME
     * Close a client socket and remove the client from the list of connected clients
     *
     * @param username: The client's username
     */
    private static synchronized void removeClient(String username) {
        ServerThread client = connectedClients.get(username);
        client.disconnect();
    }

    /**
     * Close the running server socket
     */
    private void closeServerSocket() {
        try {
            for (ServerThread client : connectedClients.values()) {
                client.disconnect();
            }
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            new Server(null, 5000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
