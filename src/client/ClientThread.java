package client;

import utils.*;

import java.io.IOException;

public class ClientThread extends AbstractThread {

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
            case CHAT:
              break;
            case CONNECTION:
              break;
            case DISCONNECTION:
              break;
            case INVALID_USERNAME:
              break;
            case USERS:
              break;
            case WHISPER:
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
}
