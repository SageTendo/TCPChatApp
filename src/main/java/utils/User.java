package utils;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Represent a user in a client-server application.
 * <p>
 * This class implements the Serializable interface, which allows user objects to be converted in
 * byte streams which can be transmitted over a network.
 *
 * @author Group4
 */
public class User implements Serializable {

  private final String username;
  private final InetAddress ip;
  /*  An identifier that is used to serialize/deserialize an object of a Serializable class */
  private static final long serialVersionUID = 420L;

  /**
   * Constructor which instantiates a user object with the following parameters
   *
   * @param username The username provided by the client
   * @param ip       The IP address of the connected client
   */
  public User(String username, InetAddress ip) {
    this.username = username;
    this.ip = ip;
  }

  /**
   * @return The username of the client
   */
  public String getUsername() {
    return username;
  }

  /**
   * @return The IP Address of the client
   */
  public InetAddress getIp() {
    return ip;
  }

  /**
   * Indicates whether some other user object is "equal to" this one.
   *
   * @see java.lang.Object#equals(Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof User)) {
      return false;
    }

    User user = (User) o;
    return this.username.equalsIgnoreCase(user.getUsername());
  }

  /**
   * @return A unique hashcode generated using the client's username.
   */
  @Override
  public int hashCode() {
    return this.username.hashCode();
  }

  /**
   * @return A String representation of the user object.
   */
  @Override
  public String toString() {
    return String.format("\nUsername: %s\n"
        + "IP: %s\n\n", username, ip);
  }
}
