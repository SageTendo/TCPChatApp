package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

/**
 * The GUILaunch program implements an application that displays a GUI for the Chat-App program.
 * Which requires users to insert a username and port number.
 *
 * @author Group4
 * @version 1.0
 * @since 2023-02-17
 */

public class GUILaunch {

  private static JButton connectButton = new JButton();
  private static JLabel enterUsernameLabel = new JLabel();
  private static JTextField enterUsername = new JTextField();
  private static JLabel portLabel = new JLabel();
  private static JTextField port = new JTextField();
  private static JFrame launchFrame = new JFrame();
  private static boolean isNumber = false;


  GUILaunch() {
    GUIcomponents();
    GUILaunchInit();
  }

  /**
   * This method is used to structure the JFrame in a way which it can fit all the necessary
   * components.
   */

  static void GUILaunchInit() {
    launchFrame.setTitle("ChatRoom");
    launchFrame.setSize(350, 230);
    launchFrame.setResizable(false);
    launchFrame.setLayout(null);
    launchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    launchFrame.setVisible(true);
  }

  /**
   * This method gives each component, a position, size and a design. Then its finally added to the
   * Jframe.
   */

  static void GUIcomponents() {
    enterUsernameLabel = new JLabel();
    enterUsernameLabel.setText("Username:");
    enterUsernameLabel.setBounds(10, 10, 100, 10);

    enterUsername = new JTextField();
    enterUsername.setBounds(10, 40, 330, 30);
    enterUsername.setFont(new Font("SansSerif", Font.PLAIN, 15));
    enterUsername.setForeground(Color.black);

    portLabel = new JLabel();
    portLabel.setText("Port number:");
    portLabel.setBounds(10, 90, 100, 10);

    port = new JTextField();
    port.setBounds(10, 110, 330, 30);
    port.setFont(new Font("SansSerif", Font.PLAIN, 15));
    port.setForeground(Color.black);

    connectButton = new JButton("Connect");
    connectButton.setBounds(125, 155, 100, 30);
    connectButton.setOpaque(false);
    connectButton.setContentAreaFilled(false);
    connectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
          if (enterUsername.getText().equals("") || enterUsername.getText().length() > 15) {
            JOptionPane.showMessageDialog(connectButton, "Please enter a valid username",
                "Attention!", JOptionPane.WARNING_MESSAGE);
          } else if (port.getText().equals("") || isNumber(port.getText()) != true) {
            JOptionPane.showMessageDialog(connectButton, "Please enter a valid port number",
                "Attention!", JOptionPane.WARNING_MESSAGE);
          } else {
            System.out.println("Connect to Server");
            ChatGUI gui = new ChatGUI(enterUsername.getText());
            launchFrame.dispose();
          }
        }
      }
    });

    launchFrame.add(enterUsernameLabel);
    launchFrame.add(enterUsername);
    launchFrame.add(portLabel);
    launchFrame.add(port);
    launchFrame.add(connectButton);
  }

  /**
   * This method checks if the input from the port field is indeed an integer.
   *
   * @param port the port number
   * @return if the port is an integer or not
   */
  private static boolean isNumber(String port) {
    try {
      int x = Integer.parseInt(port);
      isNumber = true;
    } catch (NumberFormatException e) {
      isNumber = false;
    }
    return isNumber;
  }

  /**
   * This is the main method which creates a new GUI object
   *
   * @param args Unused.
   * @return nothing
   */

  public static void main(String[] args) {

    GUILaunch GUIlaunch = new GUILaunch();

  }
}
