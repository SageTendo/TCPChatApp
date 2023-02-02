package utils;

/**
 * The types of the messages that can be sent/received by the client/server.
 */
public enum MessageType {
    // TODO: Add more type if necessary
    CONNECTION,
    DISCONNECTION,
    INVALID_USERNAME,
    USERS,
    CHAT,
    WHISPER
}
