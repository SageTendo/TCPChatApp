
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Message.MessageType.CONNECTION;
import static utils.Message.MessageType.USERS;

import client.ClientThread;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import utils.Message;


public class ClientTest {

  private final String hostname = "localhost";
  private final int port = 5000;


  public ClientThread setup() {
    try {
      ClientThread client = new ClientThread(hostname, port);
      client.start();
      return client;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testConnection() {
    ClientThread t1 = setup();
    assertTrue(t1.clientSocket.isConnected());
    t1.disconnect();
  }

  @Test
  public void testInvalidUsername() {
    Message username = new Message(
        CONNECTION, null, null, "user1");
    ClientThread t1 = setup();
    t1.sendMessage(username);

    ClientThread t2 = setup();
    t2.sendMessage(username);

    if (t1.user == null && t2.user == null) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    assertNotNull(t1.user);
    assertNull(t2.user);
    System.out.printf("t1.user -> %s", t1.user);
    System.out.printf("t2.user -> %s", t2.user);
    t1.disconnect();
    t2.disconnect();
  }

  @Test
  public void testChat() {

  }

  @Test
  public void testWhisper() {
  }

  @Test
  public void testInvalidMessage() {
  }

  @Test
  public void testNonexistentUser() {
  }

  @Test
  public void testUsers() {
    ClientThread t1 = setup();
    t1.sendMessage(new Message(CONNECTION, null, null, "testuser"));
    t1.sendMessage(new Message(USERS, null, null, null));

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    assertNotNull(t1.getConnectedUsers());
    System.out.println(t1.getConnectedUsers());
    t1.disconnect();
  }

  @Test
  public void testDisconnection() {
    ClientThread t1 = setup();
    t1.disconnect();

    try {
      t1.getMessage();
    } catch (IOException e) {
      assert true;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
