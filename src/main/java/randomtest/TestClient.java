package randomtest;

import static utils.Message.MessageType.CHAT;
import static utils.Message.MessageType.CONNECTION;
import static utils.Message.MessageType.WHISPER;

import java.io.IOException;
import java.net.Socket;
import utils.AbstractThread;
import utils.Logger;
import utils.Message;
import utils.User;

/**
 * This is a test implementation of a client thread that communicates with the server. ONLY FOR
 * TESTING PURPOSES AND WILL NOT BE PART OF THE FINAL PROJECT!
 */
public class TestClient extends AbstractThread {

  public TestClient(Socket clientSocket) throws IOException {
    super(clientSocket);
    setConnected(true);
  }

  void scanner() {
    new Thread(() -> {
      while (true) {
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
            // TODO: Handle exception
            throw new RuntimeException(ex);
          }
        } catch (ClassNotFoundException e) {
          // TODO: Handle exception
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public static void main(String[] args) {
    String IPAddress;
    int port;

    try {
      if (args.length != 3) {
        System.err.println("usage: java randomtest.TestClient <IP ADDRESS> <PORT NUMBER> "
            + "<USERNAME>");
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

      String username = args[2];
      TestClient t = new TestClient(new Socket(IPAddress, port));
      t.scanner(); //message listener
      t.sendMessage(new Message(CONNECTION, null, null, username));
      t.sendMessage(new Message(CHAT, username, null, "hello world"));
      t.sendMessage(new Message(WHISPER, username, "user2", "hello user1"));
//      t.sendMessage(new Message(DISCONNECTION, null, null, null));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
