package client;

import java.util.Arrays;
import java.util.List;
import utils.*;

import java.io.IOException;

public class ClientThread extends AbstractThread {

  private List<String> connectedUsers;

  public ClientThread(String hostname, int port) throws IOException {
    super(hostname, port);
  }

  boolean registerUser(String username) {
    return false;
  }

  @Override
  public void run() {
    // TODO: Message listening functionality
    while (this.clientSocket.isConnected()) {
      try {
        Message message = getMessage();
        //TODO: Handle different message types
        if (message != null) {
          switch (message.getType()) {
            case CONNECTION:
              setUser(new User(message.getReceiver(), clientSocket.getInetAddress()));
              break;
            case DISCONNECTION:
              disconnect();
              break;
            case INVALID_MESSAGE:
              break;
            case INVALID_USERNAME:
              break;
            case NONEXISTENT_USER:
              break;
            case USERS:
              String body = message.getBody();
              connectedUsers = Arrays.asList(body.split(Message.DELIMITER));
              break;
            case CHAT:
            case WHISPER:
              // TODO: handle chat and whisper messages
              break;
            default:
              throw new IllegalStateException("Unexpected value: " + message.getType());
          }
        } else {
          Logger.toConsole("ERROR", "NULL MESSAGE");
        }
      } catch (IOException e) {
        disconnect();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void disconnect() {
    try {
      super.disconnect();
    } catch (IOException e) {
      //TODO: Handle exception
      throw new RuntimeException(e);
    }
  }

  public List<String> getConnectedUsers() {
    return connectedUsers;
  }
}
