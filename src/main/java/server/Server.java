package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import utils.Logger;
import utils.Message;

/**
 * Server class that listens for client socket connections and handles communication between
 * clients.
 */
public class Server {

  static final int REQUIRED_USERNAME_LENGTH = 2;
  private final ServerSocket serverSocket;
  static final ConcurrentHashMap<String, ServerThread> connectedClients = new ConcurrentHashMap<>();
  private boolean serverStarted;

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
   * Send messages to a specific client (a private message)
   *
   * @param message The message to send to the client
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
   * @param username The client's username
   * @return True if the client is connected to the server, otherwise False
   */
  static boolean hasClient(String username) {
    return connectedClients.containsKey(username);
  }

  /**
   * Get a list of connected clients' usernames.
   *
   * @return The list of usernames
   */
  public static List<String> getClientUsernames() {
    List<String> usernames;
    synchronized (connectedClients) {
      usernames = new ArrayList<>(connectedClients.keySet());
    }
    return usernames;
  }

  /**
   * Closes the server socket and all connected client sockets.
   */
  private void close() {
    try {
      serverStarted = false;
      synchronized (connectedClients) {
        for (ServerThread client : connectedClients.values()) {
          client.disconnect();
        }
      }
      serverSocket.close();
    } catch (IOException e) {
      // TODO: handle exception
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    String IPAddress;
    int port;

    try {
      if (args.length != 2) {
        System.err.println("usage: java server.Server <IP ADDRESS> <PORT NUMBER>");
        System.exit(1);
      }

      /* Read in CLI arguments */
      IPAddress = args[0];
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException ignored) {
        System.err.println("Invalid port number provided...");
        System.err.println("Defaulting to port 5000");
        port = 5000;
      }

      /* Create a server instance */
      new Server(IPAddress, port);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
