package utils;

import java.io.Serializable;

/**
 * Represents a message in a client-server chat application.
 * <p>
 * This class implements the Serializable interface, which allows user objects to be converted in
 * byte streams which can be transmitted over a network.
 *
 * @author Group4
 */
public class Message implements Serializable {

  /**
   * An enumeration representing the types of messages sent between the client and server
   *
   * @author Group4
   */
  public enum MessageType {
    /* Represents a normal chat message, this is sent to all connected clients */
    CHAT,
    /* Represents a message notifying a new client of a successful login attempt */
    CONNECTION,
    /* Represents a message notifying all clients of a disconnected client */
    DISCONNECTION,
    /* Represents a message sent by the server to a client when they send an invalid or malformed
     *  message.
     */
    INVALID_MESSAGE,
    /* Represents a message sent by the server to a client tries to log in with an invalid username
     */
    INVALID_USERNAME,
    /* Represents a message sent to the client when they try to whisper to a non-existent client */
    NONEXISTENT_USER,
    /* Represents a message notifying all clients of a new client connection */
    NEW_USER,
    /**
     * Represents a message sent by the server to a client containing a list of currently connected
     * users.
     */
    USERS,
    /* Represents a private chat message sent between 2 clients, and not visible to others */
    WHISPER

  }

  private final MessageType type;
  private final String sender;
  private final String receiver;
  private final String body;
  /*  An identifier that is used to serialize/deserialize an object of a Serializable class */
  private static final long serialVersionUID = 69L;
  /* The character used to separate the elements (username) of connected clients */
  public static final String DELIMITER = ",";

  /**
   * Constructor
   *
   * @param type:     The type of message to be sent
   * @param sender:   The client sending the message
   * @param receiver: The client receiving the message (for Whisper messages)
   * @param body:     The content of the message, such as "Hello User1"
   */
  public Message(MessageType type, String sender, String receiver, String body) {
    this.type = type;
    this.sender = sender;
    this.receiver = receiver;
    this.body = body;
  }

  /**
   * @return MessageType: The type of message being sent between the client and server
   */
  public MessageType getType() {
    return type;
  }

  /**
   * @return String: The username of the client sending the message
   */
  public String getSender() {
    return sender;
  }

  /**
   * @return String: The username of the person receiving the message, in the case of whisper
   * messages
   */
  public String getReceiver() {
    return receiver;
  }

  /**
   * @return String: The content of the message.
   */
  public String getBody() {
    return body;
  }

  /**
   * EXAMPLE MESSAGE=>
   * <p>
   * TYPE: WHISPER
   * <p>
   * FROM: Alice
   * <p>
   * TO: Bob
   * <p>
   * BODY: Hi Bob, how are you doing?
   *
   * @return A string representation of the message object.
   */
  @Override
  public String toString() {
    return "MESSAGE:\n" +
        "\t TYPE: " + type + '\n' +
        "\t FROM: " + sender + '\n' +
        "\t TO: " + receiver + '\n' +
        "\t body: " + body + "\n";
  }
}
