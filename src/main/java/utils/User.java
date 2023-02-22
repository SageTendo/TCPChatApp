package utils;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {

  private final String username;
  private final InetAddress ip;
  private static final long serialVersionUID = 420L;

  /**
   * Instantiate a user object with the following parameters
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
   * @see Object#equals(Object)
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
