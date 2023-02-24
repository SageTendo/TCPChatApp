package client;

import GUI.ChatGUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import utils.AbstractThread;
import utils.Logger;
import utils.Message;
import utils.Message.MessageType;
import utils.User;

public class ClientThread extends AbstractThread {

  public List<String> connectedUsers = new ArrayList<>();

  public ClientThread(String hostname, int port) throws IOException {
    super(hostname, port);
  }

  public boolean register(String username) {
    sendMessage(new Message(MessageType.CONNECTION, null, null, username));
    try {
      Message serverResponse = getMessage();
      if (!serverResponse.getType().equals(MessageType.CONNECTION)) {
        JOptionPane.showMessageDialog(null,
            serverResponse.getBody(), "Login Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }

      setUser(new User(username, clientSocket.getInetAddress()));
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null,
          "Failed to reach server", "Connection Error", JOptionPane.ERROR_MESSAGE);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  @Override
  public void run() {
    // TODO: Message listening functionality
    while (clientSocket.isConnected()) {
      try {
        Message message = getMessage();
        if (message != null) {
          // receivemessage from server
          // maybe in the case of messagetype.user ignore it
          // format messages
          // optionpane
          // textArea.append(message.getSender()+": "+message.getBody()+"\n");
          switch (message.getType()) {
            case INVALID_MESSAGE:
              JOptionPane.showMessageDialog(null,
                  message.getBody(), "alert", JOptionPane.ERROR_MESSAGE);
              break;
            case INVALID_USERNAME:
              this.disconnect();
              JOptionPane.showMessageDialog(null,
                  message.getBody(), "alert", JOptionPane.ERROR_MESSAGE);
              break;
            case NONEXISTENT_USER:
              JOptionPane.showMessageDialog(null,
                  "Cannot whisper to a non-existent user", "alert",
                  JOptionPane.ERROR_MESSAGE);
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
          Logger.toConsole("ERROR", "NULL MESSAGE");
        }
      } catch (IOException e) {
        disconnect();
      } catch (ClassNotFoundException e) {
        //TODO: handle exception
        Logger.toConsole("SERVER", "Failed to deserialize data to a message object");
        //throw new RuntimeException(e);
      }
    }
  }


  @Override
  public void disconnect() {
    try {
      super.disconnect();
    } catch (IOException ignored) {
      // TODO: Handle exception
      //throw new RuntimeException(e);
    }
  }

  public List<String> getConnectedUsers() {
    return connectedUsers;
  }
}