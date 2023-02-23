package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.BorderFactory;
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

/**
 * The GUI program implements an application that displays a simple GUI for the Chat-App program.
 *
 * @author Group4
 * @version 1.0
 * @since 2023-02-17
 */

public class ChatGUI {

  private static JButton exitButton = new JButton();
  private static JButton sendButton = new JButton();
  private static JTextField messageField = new JTextField();
  private static JTextArea messageArea = new JTextArea();
  private static JFrame frame = new JFrame();
  private static JPanel container = new JPanel();
  private static JLabel usernameLabel = new JLabel();
  private static JTextField usernameField = new JTextField();
  private static JLabel userListLabel = new JLabel();
  private static JTextArea helpArea = new JTextArea();
  private static String username;

  public ChatGUI(String username) {
    this.username = username;
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
   * This method gives each component, a position, size and a design. Then its finally added to the
   * Jframe.
   */
  static void appComponents() {

    //Exit Button Design

    ImageIcon exit = new ImageIcon("icons/exit_50.png");
    exitButton.setBounds(10, 5, 50, 30);
    exitButton.setOpaque(false);
    exitButton.setContentAreaFilled(false);
    exitButton.setIcon(exit);
    exitButton.setFocusable(false);
    exitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
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
          frame.dispose();
        }
        System.out.println("Client has Disconnected");
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

    usernameLabel = new JLabel();
    usernameLabel.setText("Username:");
    usernameLabel.setBounds(390, 40, 110, 20);
    frame.add(usernameLabel);

    //this will display the user's current nickname

    usernameField = new JTextField();
    usernameField.setBounds(390, 60, 110, 30);
    usernameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
    usernameField.setForeground(Color.black);
    usernameField.setEditable(false);
    usernameField.setBackground(Color.white);
    usernameField.setText(username);
    frame.add(usernameField);

    //List for current online users

    userListLabel = new JLabel();
    userListLabel.setText("Online users:");
    userListLabel.setBounds(390, 120, 110, 20);
    frame.add(userListLabel);

    String[] users = {"User1", "User2", "User3"}; //just for illustration


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

    JComboBox<String> userListBox = new JComboBox<>(users);
    userListBox.setBounds(390, 150, 110, 20);
    userListBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        /**
         *
         *
         *
         * If we want an action to happen once we press on a user
         * we can do it
         *
         *
         * Here if you click on the person in the list, it automatically
         * puts "@{name}" in the messagefield
         *
         *
         *
         */
        if (e.getSource() == userListBox) {
          messageField.setText("@" + userListBox.getItemAt(userListBox.getSelectedIndex()));
        }
      }
    });
    frame.add(userListBox);

    sendButton = new JButton();
    ImageIcon send = new ImageIcon("icons/send_50.png");
    sendButton.setBounds(390, 380, 50, 30);
    sendButton.setOpaque(false);
    sendButton.setContentAreaFilled(false);
    sendButton.setIcon(send);
    sendButton.setFocusable(false);

    /**
     *
     * So here once the sendButton is pressed, whatever is in the messageField will be
     * appended to the message textArea
     *
     *
     *
     */
    sendButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
          if (messageField.getText().equals("")) {
            JOptionPane.showMessageDialog(sendButton, "Messages can not be null.", "Attention!",
                JOptionPane.WARNING_MESSAGE);
          } else {
            System.out.println(messageField.getText());
            messageArea.append(
                username + ": " + messageField.getText() + "\n"); //appended on the TextArea
          }
        }
      }
    });
    frame.add(sendButton);

    /**
     *
     *
     *
     *
     * IDK what this is, I just coded it to maybe help users
     * in terms of instructions on how to do what lol
     *
     *
     *
     *
     */
    helpArea = new JTextArea();
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

  /**
   * This is the main method which creates a new GUI object
   *
   * @param args Unused.
   * @return nothing
   */
  public static void main(String[] args) {
    ;
    //ChatGUI GUI= new ChatGUI();
  }
}
