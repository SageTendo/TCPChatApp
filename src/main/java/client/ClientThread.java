package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.xml.transform.Templates;

import utils.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends AbstractThread {

  public List<String> connectedUsers = new ArrayList<>();
  public static boolean launchChatGui = true;

  public ClientThread(String hostname, int port) throws IOException {
    super(hostname, port);
  }

  boolean registerUser(String username) {
    return false;
  }

  public void sendMessagetoServer() throws IOException, ClassNotFoundException {
    // send this.user data to server
    // scan for new messages while connected
    while (clientSocket.isConnected()) {

      Message message = getMessage();// messagetosend
      System.out.println("-send-" + message.getType() + "-");
      sendMessage(message);

    }

  }

  public void Messagelistening(JTextArea textArea) { // receivemessagefrom server
    new Thread(new Runnable() {

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
                case CONNECTION:
                  textArea.append(message.getReceiver() + " just " + message.getBody() + "\n");
                  break;
                case INVALID_MESSAGE:
                  JOptionPane.showMessageDialog(null, "alert", "malformed message", JOptionPane.ERROR_MESSAGE);
                  break;
                case INVALID_USERNAME:
                  launchChatGui = false;
                  JOptionPane.showMessageDialog(null, "alert", message.getBody(), JOptionPane.ERROR_MESSAGE);
                  break;
                // case NONEXISTENT_USER:
                // break;
                // case USERS:
                // String body = message.getBody();
                // connectedUsers = Arrays.asList(body.split(Message.DELIMITER));
                // break;
                case CHAT:
                  textArea.append(message.getSender() + ": " + message.getBody() + "\n");
                  break;
                case WHISPER:
                  // TODO: handle chat and whisper messages
                  textArea.append(message.getSender() + ": " + message.getBody() + "\n");
                  break;

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

    }).start();
  }

  @Override
  public void disconnect() {
    try {
      super.disconnect();
    } catch (IOException e) {
      // TODO: Handle exception
      throw new RuntimeException(e);
    }
  }

  public List<String> getConnectedUsers() {
    return connectedUsers;
  }
}
