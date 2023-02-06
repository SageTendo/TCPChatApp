package utils;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {

  private final MessageType type;
  private final String sender;
  private final String receiver;
  private final String body;
  @Serial
  private static final long serialVersionUID = 69L;

  /**
   * Constructor
   *
   * @param type:     The type of message to be sent
   * @param sender:   The client sending the message
   * @param receiver: The client receiving the message (for Whisper messages)
   * @param body:     The message body
   */
  public Message(MessageType type, String sender, String receiver, String body) {
    this.type = type;
    this.sender = sender;
    this.receiver = receiver;
    this.body = body;
  }

  /**
   * @return MessageType: The message type
   */
  public MessageType getType() {
    return type;
  }

  /**
   * @return String: The sender's username
   */
  public String getSender() {
    return sender;
  }

  /**
   * @return String: The receiver's username
   */
  public String getReceiver() {
    return receiver;
  }

  /**
   * @return String: The message body
   */
  public String getBody() {
    return body;
  }

  @Override
  public String toString() {
    return "MESSAGE:\n" +
        "\t TYPE: " + type + '\n' +
        "\t FROM: " + sender + '\n' +
        "\t TO: " + receiver + '\n' +
        "\t body: " + body + "\n";
  }
}
