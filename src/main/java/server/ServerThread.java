package server;

import utils.*;

import java.io.*;
import java.net.Socket;

import static server.Server.REQUIRED_USERNAME_LENGTH;
import static utils.Message.MessageType.CONNECTION;
import static utils.Message.MessageType.DISCONNECTION;
import static utils.Message.MessageType.INVALID_MESSAGE;
import static utils.Message.MessageType.INVALID_USERNAME;
import static utils.Message.MessageType.NONEXISTENT_USER;
import static utils.Message.MessageType.USERS;

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
        sendMessage(new Message(INVALID_USERNAME, "", "", errorMessage));
      }
    } catch (IOException e) {
      try {
        super.disconnect();
        String log = "Socket -> " + clientSocket.getInetAddress() + ":" + clientSocket.getPort()
            + " disconnected";
        Logger.toConsole("DISCONNECTION", log);
      } catch (IOException ex) {
        // TODO: Handle exception
        throw new RuntimeException(ex);
      }
    } catch (NullPointerException e) {
      Logger.toConsole("CLIENT ERROR", e.getMessage());
    } catch (ClassNotFoundException e) {
      // TODO: Handle exception
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
    setUser(new User(username, clientSocket.getInetAddress()));
    Server.addClient(this, username);
    sendMessage(new Message(CONNECTION, null, username, "Connected to server"));
    setConnected(true);

    // Notify all connected client of a new client connection
    String messageBody = String.format("User '%s' connected", username);
    Server.broadcastMessage(new Message(USERS, username, null, messageBody));
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
    while (isConnected()) {
      try {
        Message message = getMessage();
        //TODO: Handle different message types
        switch (message.getType()) {
          case CHAT:
            /* Send the client's message to all connected clients */
            if (message.getBody().length() != 0 && message.getSender().equals(user.getUsername())) {
              Server.broadcastMessage(message);
            }
            break;
          case DISCONNECTION:
            throw new IOException();
          case USERS:
            /* Send the list of connected clients as a string representation */
            String listAsString = String.join(Message.DELIMITER, Server.getClientUsernames());
            sendMessage(new Message(USERS, null, null, listAsString));
            break;
          case WHISPER:
            /* Send a private message to the provided client [getReceiver()] */
            if (message.getReceiver() == null) {
              String error = "No username was provided";
              sendMessage(new Message(INVALID_MESSAGE, null, null, error));
            } else if (!Server.hasClient(message.getReceiver())) {
              String error = "User " + message.getReceiver() + " does not exist";
              sendMessage(new Message(NONEXISTENT_USER, null, null, error));
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
        /* If the client is unreachable close their socket connection and remove them from the
        connected clients map */
        disconnect();

        /* Notify the other clients that a client has disconnected */
        String message = user.getUsername() + " has disconnected";
        Server.broadcastMessage(
            new Message(DISCONNECTION, null, null, user.getUsername()));
        Logger.toConsole("DISCONNECTION", message);
      } catch (NullPointerException e) {
        /* Handle null objects sent by a client */
        Logger.toConsole("Client Error", e.getMessage());
      } catch (IllegalStateException e) {
        /* Handle exception when a client sends a message with an invalid message type */
        Logger.toConsole("CLIENT ERROR", e.getMessage());
      } catch (ClassNotFoundException e) {
        // TODO: Handle exception
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
      String logMessage = "Closing socket of user: '" + user.getUsername() + "'";
      Logger.toConsole("SERVER ERROR", logMessage);
    } finally {
      Server.removeClient(user.getUsername());
    }
  }
}
