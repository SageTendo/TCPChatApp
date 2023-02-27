package client;

import GUI.ChatGUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.AbstractThread;
import utils.Logger;
import utils.Message;
import utils.Message.MessageType;
import utils.User;

/**
 * Represents a client thread that communicates with a server via sockets.
 * <p>
 * This class handles all communication made the server to a client, such as receiving messages chat
 * messages for the server or the list of connected clients. The thread runs until the client
 * disconnects from the server.
 * <p>
 * This class is a subclass of the {@link utils.AbstractThread} class.
 */
public class ClientThread extends AbstractThread {

  private List<String> connectedUsers = new ArrayList<>();

  /**
   * Constructor the takes the server's hostname and port as arguments.
   *
   * @param hostname The hostname of the server to connect to
   * @param port     The port that the server is running on
   * @throws IOException If an I/O error occurs while writing stream header.
   * @see AbstractThread#AbstractThread(String, int)
   */
  public ClientThread(String hostname, int port) throws IOException {
    super(hostname, port);
  }

  /**
   * Registers the client after establishing a socket connection with the server. The client sends a
   * {@link Message.MessageType#CONNECTION} (Connection message) to the server and waits a response
   * from the server. If the server responds with a connection message; the client's user object is
   * instantiated with the provided username and InetAddress IP, otherwise an error message will be
   * displayed to the client.
   *
   * @param username The username to register to the server with
   * @return True if registered to the server, otherwise false.
   */
  public boolean register(String username) {
    sendMessage(new Message(MessageType.CONNECTION, "", "", username));
    try {
      Message serverResponse = getMessage();
      if (!serverResponse.getType().equals(MessageType.CONNECTION)) {
        ChatGUI.showErrorMessage(serverResponse.getBody());
        return false;
      }

      setUser(new User(username, clientSocket.getInetAddress()));
      return true;
    } catch (IOException e) {
      ChatGUI.showErrorMessage("Failed to reach server");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  /**
   * Handles the listening of new messages sent by the server. The thread runs for as long as the
   * client is connected {@link AbstractThread#isConnected()} to the server.
   */
  @Override
  public void run() {
    while (clientSocket.isConnected()) {
      try {
        Message message = getMessage();
        if (message != null) {
          switch (message.getType()) {
            case INVALID_MESSAGE:
            case NONEXISTENT_USER:
              ChatGUI.showErrorMessage(message.getBody());
              break;
            case INVALID_USERNAME:
              this.disconnect();
              ChatGUI.showErrorMessage(message.getBody());
              break;
            case NEW_USER:
            case CHAT:
            case WHISPER:
            case DISCONNECTION:
              ChatGUI.updateChat(message);
              break;
            case USERS:
              String body = message.getBody();
              connectedUsers = Arrays.asList(body.split(Message.DELIMITER));
              ChatGUI.updateUsers();
              break;
          }
        } else {
          Logger.toConsole("SERVER", "NULL MESSAGE");
        }
      } catch (IOException e) {
        disconnect();
      } catch (ClassNotFoundException e) {
        Logger.toConsole("DATA CORRUPTION", "Failed to deserialize data to a message object");
      }
    }
  }

  /**
   * Disconnects the client from the server by closing the socket connection.
   *
   * @see AbstractThread#disconnect()
   */
  @Override
  public void disconnect() {
    try {
      super.disconnect();
    } catch (IOException ignored) {
      Logger.toConsole("SOCKET",
          "Attempt to close socket failed. It may have already been closed");
    }
  }

  /**
   * Getter method that returns the list of connected clients.
   *
   * @return The list of connected clients.
   */
  public List<String> getConnectedUsers() {
    return connectedUsers;
  }
}