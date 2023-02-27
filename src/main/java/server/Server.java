package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import utils.Logger;
import utils.Message;
import utils.Message.MessageType;

/**
 * Server class that listens for client socket connections and handles communication between
 * clients.
 *
 * @author Group4
 */
public class Server {

  /* The minimum length required for a client username */
  static final int REQUIRED_USERNAME_LENGTH = 2;
  /* Provides communication establishment with clients */
  private final ServerSocket serverSocket;
  /*
  Stores all connected clients and their respective usernames as key-value pairs with the
  username being the key and the client handler (server thread) as the value.
  */
  private static final ConcurrentHashMap<String, ServerThread> connectedClients =
      new ConcurrentHashMap<>();
  /* Indicates whether the server manages to open a socket */
  private final boolean serverStarted;

  /**
   * Constructor
   *
   * @param ip   THe IP Address to bind the server instance to.
   * @param port The port number that the server will run on.
   * @throws IOException - If an I/O error occurs when opening the socket.
   */
  public Server(String ip, int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    this.serverStarted = true;
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
      while (serverStarted) {
        try {
          Socket clientSocket = this.serverSocket.accept();
          String log = String.format("New socket connection -> %s:%d",
              clientSocket.getInetAddress(), clientSocket.getPort());
          Logger.toConsole("CONNECTION", log);
          new ServerThread(clientSocket).start();
        } catch (IOException e) {
          Logger.toConsole("CONNECTION ERROR",
              "Couldn't accept client socket connection");
        }
      }
    }).start();
  }

  /**
   * Sends the list of connected user to all connected clients. The list is joined using the
   * {@link utils.Message#DELIMITER} (delimiter) .
   */
  static void broadcastUsersList() {
    String listAsString = String.join(Message.DELIMITER, getClientUsernames());
    Server.broadcastMessage(new Message(MessageType.USERS, "", "", listAsString));
  }

  /**
   * Sends a message to all connected clients. The server iterates through of connected clients,
   * checks if each client is still connected to the server and sends the message to them if they're
   * still connected. This process is synchronized to prevent multiple threads from trying to send
   * messages at the same time, leading to inconsistent results.
   *
   * @param message The message to be broadcast to all connected clients
   */
  static void broadcastMessage(Message message) {
    synchronized (connectedClients) {
      for (ServerThread client : connectedClients.values()) {
        if (client.isConnected()) {
          client.sendMessage(message);
        }
      }
    }
  }

  /**
   * Sends a message to a specific client (a private message). The provided receiver in the message
   * object is used to get the thread of the client to send the message to.
   *
   * @param message The message object to send to the client
   */
  static synchronized void sendWhisperMessage(Message message) {
    ServerThread receiver = connectedClients.get(message.getReceiver());
    receiver.sendMessage(message);
  }


  /**
   * Add the new client handler to the list of connected clients and their username to the list of
   * client usernames
   *
   * @param client   The thread that handles the connected client.
   * @param username The username that the client provided.
   */
  static void addClient(ServerThread client, String username) {
    synchronized (connectedClients) {
      connectedClients.put(username, client);
    }
  }

  /**
   * Remove a client handler from the list of connected clients and their username from the list of
   * client usernames
   *
   * @param username: The client's username
   */
  static void removeClient(String username) {
    synchronized (connectedClients) {
      connectedClients.remove(username);
    }
  }

  /**
   * Checks if a client with the given username is connected to the server (exists in the map of
   * connected clients).
   *
   * @param username The username of the client to lookup
   * @return True if the client is connected to the server, otherwise False
   */
  static boolean hasClient(String username) {
    return connectedClients.containsKey(username);
  }

  /**
   * Returns a list of usernames of clients currently connected. This is done by connected the map's
   * keySet to an Arraylist.
   *
   * @return The list of usernames of connected clients
   */
  public static List<String> getClientUsernames() {
    List<String> usernames;
    synchronized (connectedClients) {
      usernames = new ArrayList<>(connectedClients.keySet());
    }
    return usernames;
  }
}
