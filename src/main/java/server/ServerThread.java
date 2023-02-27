package server;

import utils.*;

import java.io.*;
import java.net.Socket;
import utils.Message.MessageType;

import static server.Server.REQUIRED_USERNAME_LENGTH;

/**
 * Represent a server thread that communicates with a client via sockets.
 * <p>
 * This class handles all communication made by connected clients to the server, such as messages
 * sent by a client or when a client disconnects. Each client is handled in a separate thread, which
 * is killed once the client disconnects.
 * <p>
 * This class is a subclass of the {@link utils.AbstractThread} class.
 *
 * @author Group4
 */
public class ServerThread extends AbstractThread {

  /**
   * Constructor that takes a client's socket as an argument.
   *
   * @param socket The client's socket connection
   * @see AbstractThread#AbstractThread(Socket)
   */
  public ServerThread(Socket socket) throws IOException {
    super(socket);
  }

  /**
   * Validate the username provided by the client. If invalid, notify the client of the validation
   * error if valid, instantiate the user object with the client's username and InetAddress, and
   * then add the ServerThread instance to the list of connected clients and their username to the
   * list of client usernames.
   */
  private void validateUsername() {
    try {
      String errorMessage = "";
      boolean validUsername = true;

      Message clientMessage = getMessage();
      String username = clientMessage.getBody();
      if (username == null) {
        validUsername = false;
        errorMessage = "Username can't be null";
      } else if (username.length() < REQUIRED_USERNAME_LENGTH) {
        validUsername = false;
        errorMessage = "Username must be more than 1 character";
      } else if (!username.matches("[a-z0-9_-]+")) {
        validUsername = false;
        errorMessage = "Username can only contain lowercase characters, digits, hyphens, underscores";
      } else if (Server.hasClient(username)) {
        validUsername = false;
        errorMessage = "Username is already taken";
      }

      if (validUsername) {
        this.registerClient(username);
      } else {
        sendMessage(new Message(MessageType.INVALID_USERNAME, "", "", errorMessage));
      }
    } catch (IOException e) {
      try {
        super.disconnect();
        String log = "Socket -> " + clientSocket.getInetAddress() + ":" + clientSocket.getPort()
            + " disconnected";
        Logger.toConsole("DISCONNECTION", log);
      } catch (IOException ignored) {
        String logMessage = "Closing socket of user: '" + user.getUsername() + "'";
        Logger.toConsole("SERVER ERROR", logMessage);
      }
    } catch (NullPointerException e) {
      Logger.toConsole("CLIENT ERROR", e.getMessage());
    } catch (ClassNotFoundException e) {
      Logger.toConsole("DATA CORRUPTION", "Failed to deserialize data to a message object");
    }
  }

  /**
   * Register client to the server. This entails; creating a user instance to hold certain
   * information about the client, adding the client socket to the map of connected clients, adding
   * the clients username to the list of client username, notifying other clients of a new user
   * joining the chat and sending the list of connected.
   *
   * @param username THe username provided by the client
   */
  synchronized void registerClient(String username) {
    setUser(new User(username, clientSocket.getInetAddress()));
    Server.addClient(this, username);
    sendMessage(new Message(MessageType.CONNECTION, "", username, "Connected to server"));
    setConnected(true);

    // Notify all connected client of a new client connection
    String messageBody = String.format("'%s' connected", username);
    Server.broadcastMessage(new Message(MessageType.NEW_USER, "", "", messageBody));

    Server.broadcastUsersList(); // Send the list of connected users to all clients
    String log = String.format("%s:%d -> ", clientSocket.getInetAddress(), clientSocket.getPort());
    Logger.toConsole("REGISTRATION", log + messageBody);
  }

  /**
   * Handles the listening of new messages sent by the client. The method calls
   * {@link ServerThread#validateUsername} which will wait for the client to respond with a valid
   * username. If the username supplied is valid the client is registered and the while loop will
   * run for as long as the client is connected, otherwise the client connection is closed.
   */
  @Override
  public void run() {
    validateUsername();
    while (isConnected()) {
      try {
        Message message = getMessage();
        switch (message.getType()) {
          case CHAT:
            /* Send the client's message to all connected clients */
            if (message.getBody().length() != 0 && message.getSender().equals(user.getUsername())) {
              Server.broadcastMessage(message);
            }
            break;
          case WHISPER:
            /* Send a private message to the provided client [getReceiver()] */
            if (message.getReceiver() == null) {
              String error = "No username was provided";
              sendMessage(new Message(MessageType.INVALID_MESSAGE, "", "", error));
            } else if (!Server.hasClient(message.getReceiver())) {
              String error = "Cannot whisper to a non-existent user";
              sendMessage(new Message(MessageType.NONEXISTENT_USER, "", "", error));
            } else if (message.getReceiver().equals(user.getUsername())) {
              String error = "You cannot whisper to yourself";
              sendMessage(new Message(MessageType.INVALID_MESSAGE, "", "", error));
            } else {
              sendMessage(message); //Send the message back to the sender
              Server.sendWhisperMessage(message);
            }
            break;
          default:
            throw new IllegalStateException(
                "User " + user.getUsername() + " sent an invalid message " + "type: "
                    + message.getType());
        }
      } catch (IOException e) {
        /* If the client is unreachable close their socket connection and remove them from the
        connected clients map */
        disconnect();

        /* Notify the other clients that a client has disconnected */
        String message = user.getUsername() + " has disconnected";
        Server.broadcastMessage(
            new Message(MessageType.DISCONNECTION, "", "", message));

        /* update client user list */
        Server.broadcastUsersList();
        Logger.toConsole("DISCONNECTION", message);
      } catch (NullPointerException e) {
        /* Handle null objects sent by a client */
        Logger.toConsole("CLIENT ERROR", e.getMessage());
      } catch (IllegalStateException e) {
        /* Handle exception when a client sends a message with an invalid message type */
        Logger.toConsole("CLIENT ERROR", e.getMessage());
      } catch (ClassNotFoundException e) {
        Logger.toConsole("DATA CORRUPTION",
            "Failed to deserialize data to a message object");
      }
    }
  }

  /**
   * Disconnects the client socket and removes them from the connected clients map.
   */
  @Override
  public void disconnect() {
    try {
      super.disconnect();
    } catch (IOException e) {
      String logMessage = "Closing socket of user: '" + user.getUsername() + "'";
      Logger.toConsole("SERVER ERROR", logMessage);
    } finally {
      Server.removeClient(user.getUsername());
    }
  }
}
