package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A subclass of the {@link Thread} class. Provides common functionalities that can be used by
 * multiple threads.
 *
 * @author Group4
 */
public abstract class AbstractThread extends Thread {

  public final Socket clientSocket;
  public User user;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  /* Indicates the status of a socket connection */
  private boolean isConnected = false;

  /**
   * Constructor that takes a client's socket as an argument.
   *
   * @param clientSocket The socket connection between the server and client
   * @throws IOException If an I/O error occurs while writing stream header.
   */
  public AbstractThread(Socket clientSocket) throws IOException {
    this.clientSocket = clientSocket;
    createIOStreams();
  }

  /**
   * Constructor that takes the server's hostname and port number that the server is running on, as
   * arguments.
   *
   * @param hostname The server's hostname
   * @param port     The port the server runs on
   * @throws IOException If an I/O error occurs while writing stream header.
   */
  public AbstractThread(String hostname, int port) throws IOException {
    this.clientSocket = new Socket(hostname, port);
    createIOStreams();
  }

  /**
   * Instantiate the output and input streams (IN THIS ORDER -> To avoid blocking caused by
   * instantiating the ObjectInputStream as it tries to read the object stream header from the
   * InputStream).
   *
   * @throws IOException If an I/O error occurs while writing stream header.
   */
  private void createIOStreams() throws IOException {
    this.out = new ObjectOutputStream(clientSocket.getOutputStream());
    this.in = new ObjectInputStream(clientSocket.getInputStream());
  }

  /**
   * Handle disconnecting the client and removing their thread instance from the list of connected
   * clients and their username from the list of client usernames.
   *
   * @throws IOException: If an I/O error occurs when closing the socket.
   */
  public void disconnect() throws IOException {
    this.isConnected = false;
    this.clientSocket.close();
  }

  /**
   * Sends serialized message objects between the client and server.
   *
   * @param message Message: The message object to be serialized and sent by the client/server
   */
  public void sendMessage(Message message) {
    try {
      this.out.writeObject(message);
      this.out.flush();
    } catch (IOException ignored) {
    }
  }

  /**
   * Sets the user object containing the connected client's information, such as their username and
   * IP Address.
   *
   * @param user The user object to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Read and return a message object from the input stream
   *
   * @return The message received
   * @throws IOException            Input/Output related exceptions
   * @throws ClassNotFoundException WHen the class of a serialized object cannot be found.
   */
  public synchronized Message getMessage() throws IOException, ClassNotFoundException {
    Message message = (Message) in.readObject();
    if (message == null) {
      throw new NullPointerException(
          String.format("User '%s' sent null as an object", this.user.getUsername())
      );
    }
    return message;
  }

  /**
   * @return True if the socket connection is still alive, otherwise false.
   */
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Setter method to set the 'isConnected' flag.
   *
   * @param connected The boolean flag to set the 'isConnected' flag to.
   */
  public void setConnected(boolean connected) {
    isConnected = connected;
  }
}
