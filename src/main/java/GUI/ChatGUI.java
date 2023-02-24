package GUI;

import client.ClientThread;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import utils.Message;
import utils.Message.MessageType;

/**
 * The GUI program implements an application that displays a simple GUI for the Chat-App program.
 *
 * @author Group4
 * @version 1.0
 * @since 2023-02-17
 */

public class ChatGUI {

  private static ClientThread clientThread;
  private static JComboBox<String> userListBox;
  private static DefaultComboBoxModel<String> userListModel;
  private static final JButton exitButton = new JButton();
  private static JButton sendButton = new JButton();
  private static JTextField messageField = new JTextField();
  private static JTextArea messageArea = new JTextArea();
  private static final JFrame frame = new JFrame();
  private static final JPanel container = new JPanel();
  private static String username;

  public ChatGUI(ClientThread clientThread) {
    ChatGUI.clientThread = clientThread;
    username = clientThread.user.getUsername();
    appComponents();
    init();
  }

  /**
   * This method is used to structure the JFrame in a way which it can fit all the necessary
   * components.
   */
  static void init() {
    frame.setTitle("ChatRoom");
    frame.setSize(520, 460);
    frame.setResizable(false);
    frame.setLayout(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * This method gives each component, a position, size and a design. Then it's finally added to the
   * JFrame.
   */
  static void appComponents() {
    String path = System.getProperty("user.dir") + "/GUI";

    //Exit Button Design
    ImageIcon exit = new ImageIcon(path + "/icons/exit_50.png");
    exitButton.setBounds(10, 5, 50, 30);
    exitButton.setOpaque(false);
    exitButton.setContentAreaFilled(false);
    exitButton.setIcon(exit);
    exitButton.setFocusable(false);
    exitButton.addActionListener(e -> {
      /*
       *
       *
       *
       *
       *
       * CODE TO BE INSERTED:
       * USER PRESSES DISCONNECT
       * PROGRAM ENDS
       *
       *
       *
       *
       *
       *
       *
       */
      if (e.getSource() == exitButton) {
        clientThread.disconnect();
        frame.dispose();
        frame.setVisible(false);
        System.exit(0);
      }
    });
    frame.add(exitButton);

    // Design of the area that will display messages that are sent and received.
    messageArea = new JTextArea(16, 19);
    messageArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
    messageArea.setForeground(Color.black);
    messageArea.setEditable(false);
    Border border = BorderFactory.createLineBorder(Color.gray);
    messageArea.setBorder(BorderFactory.createCompoundBorder(border,
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    messageArea.setLineWrap(true);

    // Adding a scroll incase the messages get to the end of the area
    JScrollPane scroll = new JScrollPane(messageArea);

    //Using a JPanel to sort of group the textArea and the scroller together
    container.setBounds(50, 45, 320, 320);
    container.add(scroll);
    frame.add(container);

    //Messages will be typed in this field
    messageField = new JTextField();
    messageField.setBounds(50, 380, 320, 30);
    messageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
    messageField.setForeground(Color.black);
    frame.add(messageField);

    JLabel usernameLabel = new JLabel();
    usernameLabel.setText("Username:");
    usernameLabel.setBounds(390, 40, 110, 20);
    frame.add(usernameLabel);

    //this will display the user's current nickname
    JTextField usernameField = new JTextField();
    usernameField.setBounds(390, 60, 110, 30);
    usernameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
    usernameField.setForeground(Color.black);
    usernameField.setEditable(false);
    usernameField.setBackground(Color.white);
    usernameField.setText(username);
    frame.add(usernameField);

    //List for current online users
    JLabel userListLabel = new JLabel();
    userListLabel.setText("Online users:");
    userListLabel.setBounds(390, 120, 110, 20);
    frame.add(userListLabel);

    /*
     *
     * If we have an arraylist or something for the users we can
     * add that list in the comboBox
     *
     * If a new user joins we can use userListBox.addItem()
     *
     *
     *          *
     *
     * Alternatively we could just have a command that the user types in
     * to get a view of the list of users in the message area
     *
     */

    userListModel = new DefaultComboBoxModel<>();
    userListModel.addAll(clientThread.getConnectedUsers());
    userListBox = new JComboBox<>(userListModel);
    userListBox.setBounds(390, 150, 110, 20);
    userListBox.addActionListener(e -> {
      /*
       
       
       
        If we want an action to happen once we press on a user
        we can do it
       
       
        Here if you click on the person in the list, it automatically
        puts "@{name}" in the messagefield
       
       
       
       */
      if (e.getSource() == userListBox) {
        if (userListBox.getItemAt(userListBox.getSelectedIndex()) != null) {
          messageField.setText("@" + userListBox.getItemAt(userListBox.getSelectedIndex()) + " ");
        }
      }
    });
    frame.add(userListBox);

    sendButton = new JButton();
    ImageIcon send = new ImageIcon(path + "/icons/send_50.png");
    sendButton.setBounds(390, 380, 50, 30);
    sendButton.setOpaque(false);
    sendButton.setContentAreaFilled(false);
    sendButton.setIcon(send);
    sendButton.setFocusable(false);

    /*
     
      So here once the sendButton is pressed, whatever is in the messageField will be
      appended to the message textArea
     
     
     
     */
    sendButton.addActionListener(e -> {
      if (e.getSource() == sendButton) {
        if (messageField.getText().equals("")) {
          JOptionPane.showMessageDialog(sendButton,
              "Messages can not be null.", "Attention!", JOptionPane.WARNING_MESSAGE);
        } else {
          String textFieldContent = messageField.getText();
          String receiver;
          String messageContent;
          if (textFieldContent.contains("@")) {
            /* Send whisper message */
            receiver = textFieldContent.substring(1, textFieldContent.indexOf(" "));
            messageContent = textFieldContent.substring(textFieldContent.indexOf(" "));
            sendMessage(
                new Message(MessageType.WHISPER, clientThread.user.getUsername(), receiver,
                    messageContent));
          } else {
            /* Send a normal message */
            sendMessage(
                new Message(MessageType.CHAT, clientThread.user.getUsername(), null,
                    textFieldContent));
          }
          /* Clear the message field when the message is sent  */
          messageField.setText("");
        }
      }
    });
    frame.add(sendButton);

    /*
     
     
     
     
      IDK what this is, I just coded it to maybe help users
      in terms of instructions on how to do what lol
     
     
     
     
     */
    JTextArea helpArea = new JTextArea();
    helpArea.setBounds(390, 190, 110, 140);
    helpArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
    helpArea.setForeground(Color.black);
    helpArea.setEditable(false);
    helpArea.setText("For Whispers:\n\nType:\n\n'@(name)'\n\nE.g @Kyle Hi");
    Border border1 = BorderFactory.createLineBorder(Color.gray);
    helpArea.setBorder(BorderFactory.createCompoundBorder(border1,
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    helpArea.setLineWrap(true);
    helpArea.setEditable(false);
    frame.add(helpArea);
  }

  private static void sendMessage(Message message) {
    clientThread.sendMessage(message);
  }

  public static void updateUsers() {
    userListModel.removeAllElements();
    userListModel.addAll(clientThread.getConnectedUsers());
  }

  public static void updateChat(Message message) {
    String sender = message.getSender();
    String content = message.getBody();
    switch (message.getType()) {
      case CHAT:
      case WHISPER:
        messageArea.append(sender + ": " + content + "\n");
        break;
      case NEW_USER:
      case DISCONNECTION:
        messageArea.append(content + '\n');
    }
  }
}
