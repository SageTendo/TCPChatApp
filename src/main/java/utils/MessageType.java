package utils;

/**
 * The types of the messages that can be sent/received by the client/server.
 */
public enum MessageType {
  // TODO: Add more types if necessary
  CONNECTION,
  DISCONNECTION,
  INVALID_MESSAGE,
  INVALID_USERNAME,
  NONEXISTENT_USER,
  USERS,
  CHAT,
  WHISPER
}
