import GUI.GUILaunch;
import java.io.IOException;
import server.Server;
import utils.Logger;

/**
 * Driver class for a TCP chat program. The program can be run in 2 modes, client or server. This is
 * determined using CLI arguments.
 * <p>
 * CLIENT MODE: java TCPChatApp client
 * <p>
 * SERVER MODE: java TCPChatApp server [IP ADDRESS] [PORT NUMBER]
 *
 * @author Group4
 */
public class TCPChatApp {

  public static void main(String[] args) {
    String IPAddress;
    int port;

    /* Check if CLI arguments were given */
    if (args.length < 1) {
      System.err.println("usage: java TCPChatApp <MODE>");
      System.err.println("MODE: client / server");
      System.exit(1);
    }

    if (args[0].equals("server")) {
      /* Run in server mode */

      /* Check if the IPAddress and Port have been provided as CLI arguments */
      if (args.length != 3) {
        System.err.println("usage: java TCPChatAPP <IP ADDRESS> <PORT NUMBER>");
        System.exit(1);
      }

      try {
        /* Read in CLI arguments */
        IPAddress = args[1];
        try {
          port = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {
          System.err.println("Invalid port number provided...");
          System.err.println("Defaulting to port 5000");
          port = 5000;
        }

        /* Create a server instance */
        new Server(IPAddress, port);
      } catch (IOException e) {
        Logger.toConsole("SERVER ERROR", "Failed to start the server");
      }
    } else if (args[0].equals("client")) {
      /* Run in client mode */
      new GUILaunch();
    }
  }
}
