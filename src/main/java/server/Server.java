package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import utils.Logger;
import utils.Message;

public class Server {

  static final int REQUIRED_USERNAME_LENGTH = 2;
  private final ServerSocket serverSocket;
  static final HashMap<String, ServerThread> connectedClients = new HashMap<>();
  static final ArrayList<String> clientUsernames = new ArrayList<>();


  public Server(String ip, int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    newClientConnectionListener();

    String hostname = ip == null ? "localhost" : ip;
    Logger.toConsole("SERVER", String.format("Server Started on %s:%d", hostname, port));
  }

  /**
   * Listens for new client socket connections. When a new client connection is accepted, a new
   * thread (Server Thread) is created to handle communication between the client and the server.
   */
  private void newClientConnectionListener() {
    new Thread(() -> {
      while (true) {
        try {
          Socket clientSocket = this.serverSocket.accept();
          String log = String.format("New socket connection -> %s:%d",
              clientSocket.getInetAddress(), clientSocket.getPort()
          );
          Logger.toConsole("CONNECTION", log);
          new ServerThread(clientSocket).start();
        } catch (IOException e) {
          //TODO: Handle exception
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
   * Send messages to a specific client (a private message)
   *
   * @param message The message to send to the client
   */
  static synchronized void sendWhisperMessage(Message message) {
    new Thread(() -> {
      ServerThread receiver = connectedClients.get(message.getReceiver());
      receiver.sendMessage(message);
    }).start();
  }


  /**
   * Add the new client handler to the list of connected clients and their username to the list of
   * client usernames
   *
   * @param client   The thread that handles the connected client.
   * @param username The username that the client provided.
   */
  static synchronized void addClient(ServerThread client, String username) {
    clientUsernames.add(username);
    connectedClients.put(username, client);
  }

  /**
   * Remove a client handler from the list of connected clients and their username from the list of
   * client usernames
   *
   * @param username: The client's username
   */
  static synchronized void removeClient(String username) {
    connectedClients.remove(username);
    clientUsernames.remove(username);
  }

  static synchronized boolean hasClient(String username) {
    return connectedClients.containsKey(username);
  }

  /**
   * Close the running server socket
   */
  private void close() {
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
