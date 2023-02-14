package server;

import utils.*;

import java.io.*;
import java.net.Socket;

import static server.Server.REQUIRED_USERNAME_LENGTH;

public class ServerThread extends AbstractThread {

  public ServerThread(Socket socket) throws IOException {
    super(socket);
  }

  /**
   * Validate the username provided by the client. If invalid, notify the client of the validation
   * error if valid, instantiate the user object with the client's username and InetAddress, and
   * then add the ServerThread instance to the list of connected clients and their username to the
   * list of client usernames.
   */
  public void validateUsername() {
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
      } else if (Server.clientUsernames.contains(username)) {
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
        String log = String.format("Socket -> %s:%s disconnected", clientSocket.getInetAddress(),
            clientSocket.getPort());
        Logger.toConsole("DISCONNECTION", log);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    } catch (NullPointerException e) {
      Logger.toConsole("Client Error", e.getMessage());
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
    while (isConnected) {
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
            String listAsString = String.join(",", Server.clientUsernames);
            sendMessage(
                new Message(MessageType.USERS, null, null, listAsString));
            break;
          case WHISPER:
            String errorMessage = null;
            boolean messageValid = true;
            if (message.getReceiver() == null) {
              errorMessage = "No username was provided";
              messageValid = false;
            } else if (!Server.hasClient(message.getReceiver())) {
              errorMessage = String.format("User '%s' does not exist", message.getReceiver());
              messageValid = false;
            }
            if (messageValid) {
              this.sendMessage(message); //Send the message back to the sender
              Server.sendWhisperMessage(message);
            } else {
              sendMessage(
                  new Message(MessageType.NONEXISTENT_USER, null, null, errorMessage));
            }
            break;
          default:
            throw new IllegalStateException(
                String.format("User '%s' sent an invalid message type: %s", this.user.getUsername(),
                    message.getType()));
        }
      } catch (IOException e) {
        this.disconnect();
        String message = String.format("%s has disconnected", this.user.getUsername());
        Server.broadcastMessage(
            new Message(
                MessageType.DISCONNECTION, null, null, this.user.getUsername()));
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

  @Override
  public synchronized void disconnect() {
    try {
      super.disconnect();
    } catch (IOException e) {
      Logger.toConsole("SERVER ERROR",
          String.format("Closing socket of user: '%s'", this.user.getUsername()));
    } finally {
      Server.removeClient(this.user.getUsername());
      this.isConnected = false;
    }
  }
}
