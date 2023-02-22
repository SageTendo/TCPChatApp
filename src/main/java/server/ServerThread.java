package server;

import utils.*;

import java.io.*;
import java.net.Socket;

import static server.Server.REQUIRED_USERNAME_LENGTH;

/**
 * A subclass of the AbstractThread. This handles each client connection to the chat server, by
 * creating a separate thread for each client.
 */
public class ServerThread extends AbstractThread {

  /**
   * @see utils.AbstractThread#AbstractThread(Socket)
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
        this.sendMessage(
            new Message(MessageType.INVALID_USERNAME, "", "", errorMessage));
      }
    } catch (IOException e) {
      try {
        super.disconnect();
        String log = "Socket -> " + clientSocket.getInetAddress() + ":" + clientSocket.getPort()
            + " disconnected";
        Logger.toConsole("DISCONNECTION", log);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    } catch (NullPointerException e) {
      Logger.toConsole("CLIENT ERROR", e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
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
    this.setUser(new User(username, clientSocket.getInetAddress()));
    Server.addClient(this, username);
    this.sendMessage(
        new Message(MessageType.CONNECTION, null, username, "Connected to server"));
    this.isConnected = true;

    // Notify all connected client of a new client connection
    String messageBody = String.format("User '%s' connected", username);
    Server.broadcastMessage(new Message(MessageType.USERS, username, null, messageBody));
    String log = String.format("%s:%d -> ", clientSocket.getInetAddress(), clientSocket.getPort())
        + messageBody;
    Logger.toConsole("REGISTRATION", log);
  }

  /**
   * Handle new messages sent by the client
   */
  @Override
  public void run() {
    validateUsername();
    while (this.isConnected) {
      try {
        Message message = getMessage();
        //TODO: Handle different message types
        switch (message.getType()) {
          case CHAT:
            if (message.getBody().length() != 0) {
              Server.broadcastMessage(message);
            }
            break;
          case DISCONNECTION:
            throw new IOException();
          case USERS:
            String listAsString = String.join(Message.DELIMITER, Server.getClientUsernames());
            sendMessage(new Message(MessageType.USERS, null, null, listAsString));
            break;
          case WHISPER:
            if (message.getReceiver() == null) {
              String error = "No username was provided";
              sendMessage(new Message(MessageType.NONEXISTENT_USER, null, null, error));
            } else if (!Server.hasClient(message.getReceiver())) {
              String error = "User " + message.getReceiver() + " does not exist";
              sendMessage(new Message(MessageType.NONEXISTENT_USER, null, null, error));
            } else {
              sendMessage(message); //Send the message back to the sender
              Server.sendWhisperMessage(message);
            }
            break;
          default:
            throw new IllegalStateException(
                "User " + user.getUsername() + " sent an invalid message "
                    + "type: " + message.getType());
        }
      } catch (IOException e) {
        disconnect();
        String message = String.format("%s has disconnected", user.getUsername());
        Server.broadcastMessage(
            new Message(MessageType.DISCONNECTION, null, null, user.getUsername()));
        Logger.toConsole("DISCONNECTION", message);
      } catch (NullPointerException e) {
        Logger.toConsole("Client Error", e.getMessage());
      } catch (IllegalStateException e) {
        Logger.toConsole("CLIENT ERROR", e.getMessage());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
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
      Logger.toConsole("SERVER ERROR",
          "Closing socket of user: '" + user.getUsername() + "'");
    } finally {
      Server.removeClient(user.getUsername());
    }
  }
}
