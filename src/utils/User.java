package utils;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {

  private final String username;
  private final InetAddress ip;

  public User(String username, InetAddress ip) {
    this.username = username;
    this.ip = ip;
  }

  public String getUsername() {
    return username;
  }

  public InetAddress getIp() {
    return ip;
  }

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

  @Override
  public int hashCode() {
    return this.username.hashCode();
  }
}
