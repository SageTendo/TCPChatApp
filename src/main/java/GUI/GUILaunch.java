package GUI;

import client.ClientThread;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * The GUILaunch program implements an application that displays a GUI for the Chat-App program.
 * Which requires users to insert a username, the server's hostname and port number.
 *
 * @author Group4
 */

public class GUILaunch {

  private static JButton connectButton;
  private static JTextField enterUsername;
  private static JTextField IPAddressEntry;
  private static JTextField portEntry;
  private static final JFrame launchFrame = new JFrame();


  /**
   * Constructor - Calls for the creation frame components and launching the frame
   */
  public GUILaunch() {
    GUIComponents();
    GUILaunchInit();
  }

  /**
   * This method is used to structure the JFrame in a way which it can fit all the necessary
   * components.
   */
  static void GUILaunchInit() {
    launchFrame.setTitle("ChatRoom");
    launchFrame.setSize(350, 300);
    launchFrame.setResizable(false);
    launchFrame.setLayout(null);
    launchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    launchFrame.setVisible(true);
  }

  /**
   * This method gives each component, a position, size and a design. Then it's finally added to the
   * JFrame.
   */
  static void GUIComponents() {
    JLabel enterUsernameLabel = new JLabel("Username:");
    enterUsernameLabel.setBounds(10, 20, 100, 10);

    enterUsername = new JTextField();
    enterUsername.setBounds(10, 40, 330, 30);
    enterUsername.setFont(new Font("SansSerif", Font.PLAIN, 15));
    enterUsername.setForeground(Color.black);

    JLabel IPAddressLabel = new JLabel("IP Address:");
    IPAddressLabel.setBounds(10, 90, 100, 10);

    IPAddressEntry = new JTextField("localhost");
    IPAddressEntry.setBounds(10, 110, 330, 30);
    IPAddressEntry.setFont(new Font("SansSerif", Font.PLAIN, 15));
    IPAddressEntry.setForeground(Color.black);

    JLabel portLabel = new JLabel("Port Number:");
    portLabel.setBounds(10, 160, 100, 10);

    portEntry = new JTextField("5000");
    portEntry.setBounds(10, 180, 330, 30);
    portEntry.setFont(new Font("SansSerif", Font.PLAIN, 15));
    portEntry.setForeground(Color.black);

    connectButton = new JButton("Connect");
    connectButton.setBounds(125, 225, 100, 30);
    connectButton.setOpaque(false);
    connectButton.setContentAreaFilled(false);
    connectButton.addActionListener(e -> {
      if (e.getSource() == connectButton) {
        if (enterUsername.getText().equals("") || enterUsername.getText().length() > 15) {
          JOptionPane.showMessageDialog(connectButton, "Please enter a valid username",
              "Attention!", JOptionPane.WARNING_MESSAGE);
        } else if (IPAddressEntry.getText().equals("")) {
          JOptionPane.showMessageDialog(connectButton,
              "Please enter the hostname of the server", "Attention!",
              JOptionPane.WARNING_MESSAGE);
        } else if (portEntry.getText().equals("") || !isNumber(portEntry.getText())) {
          JOptionPane.showMessageDialog(connectButton, "Please enter a valid port number",
              "Attention!", JOptionPane.WARNING_MESSAGE);
        } else {
          String IPAddress = IPAddressEntry.getText();
          int port = Integer.parseInt(portEntry.getText());
          String username = enterUsername.getText();

          try {
            ClientThread clientThread = new ClientThread(IPAddress, port);
            /* Run the registration process in a separate thread to prevent the GUI from halting */
            new Thread(() -> {
              if (clientThread.register(username)) {
                launchFrame.dispose();
                /* Start the GUI before message listening */
                new ChatGUI(clientThread);
                clientThread.start();
              }
            }).start();
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(launchFrame, "Failed to connect to server",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    /* Add components to the launch frame */
    launchFrame.add(enterUsernameLabel);
    launchFrame.add(enterUsername);
    launchFrame.add(IPAddressLabel);
    launchFrame.add(IPAddressEntry);
    launchFrame.add(portLabel);
    launchFrame.add(portEntry);
    launchFrame.add(connectButton);
  }

  /**
   * This method checks if the input from the port field is indeed an integer.
   *
   * @param port the port number
   * @return true if the port is an integer, otherwise false
   */
  private static boolean isNumber(String port) {
    try {
      Integer.parseInt(port);
      return true;
    } catch (NumberFormatException ignored) {
    }
    return false;
  }
}