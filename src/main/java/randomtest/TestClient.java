package randomtest;

import utils.AbstractThread;
import utils.Logger;
import utils.Message;
import utils.MessageType;

import java.io.IOException;
import java.net.Socket;
import utils.User;

/**
 * This is a test implementation of a client thread that communicates with the server. ONLY FOR
 * TESTING PURPOSES AND WILL NOT BE PART OF THE FINAL PROJECT!
 */
public class TestClient extends AbstractThread {

  public TestClient(Socket clientSocket) throws IOException {
    super(clientSocket);
    isConnected = true;
  }

  void scanner() {
    new Thread(() -> {
      while (isConnected) {
        try {
          Message message = getMessage();
          switch (message.getType()) {
            case CONNECTION:
              Logger.toConsole("CONNECTION", message.getBody());
              this.setUser(new User(message.getReceiver(), this.clientSocket.getInetAddress()));
              break;
            case DISCONNECTION:
              break;
            case INVALID_USERNAME:
              Logger.toConsole("INVALID USERNAME", message.getBody());
              System.exit(0);
              break;
            case USERS:
              Logger.toConsole("USERS", message.getBody());
              break;
            case CHAT:
            case WHISPER:
              Logger.toConsole("CHAT", message.getBody());
              break;
          }
          System.out.println(message);
        } catch (IOException e) {
          try {
            super.disconnect();
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
//          throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public static void main(String[] args) {
    try {
      TestClient t = new TestClient(new Socket("localhost", 5000));
      t.scanner(); //message listener
      t.sendMessage(new Message(MessageType.CONNECTION, null, null, args[0]));
      t.sendMessage(new Message(MessageType.CHAT, args[0], null, "hello world"));
      t.sendMessage(new Message(MessageType.WHISPER, args[0], "user2", "hello user1"));
      t.sendMessage(new Message(MessageType.DISCONNECTION, null, null, null));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
