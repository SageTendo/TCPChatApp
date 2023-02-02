package server;

import utils.*;

import java.io.*;
import java.net.Socket;

import static server.Server.REQUIRED_USERNAME_LENGTH;

public class ServerThread extends Thread {
    private final Socket clientSocket;
    private boolean isConnected;
    private final PrintWriter out;
    private final BufferedReader in;
    private User user;

    public ServerThread(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate the username provided by the client.
     * If invalid, notify the client of the validation error
     * if valid, instantiate the user object with the client's username and InetAddress, and then add the
     * ServerThread instance to the list of connected clients and their username to the list of client usernames.
     */
    private synchronized void validateUsername() {
        try {
            InputStream clientInputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientInputStream));

            String username;
            String messageBody;
            while (true) {
                username = bufferedReader.readLine();
                if (username == null) {
                    disconnect();
                    String message = String.format("Socket {%s} Disconnected", clientSocket.getInetAddress());
                    Logger.toConsole("DISCONNECTION", message);
                    return;
                }

                if (username.length() < REQUIRED_USERNAME_LENGTH) {
                    messageBody = "Username must be more than 1 character...";
                } else if (!username.matches("[a-z0-9_-]+")) {
                    messageBody = "Username can only contain lowercase characters, digits, hyphens, underscores...";
                } else if (Server.clientUsernames.contains(username)) {
                    messageBody = "Username already taken...";
                } else {
                    // Valid username
                    this.user = new User(username, clientSocket.getInetAddress());
                    Server.addClient(this, username);
                    this.isConnected = true;
                    break;
                }
                sendMessage(new Message(MessageType.INVALID_USERNAME, "", "", messageBody));
            }

            sendMessage(new Message(MessageType.CONNECTION, null, null, "Connected successfully"));
            String message = String.format("User '%s' connected...", username);
            Server.broadcastMessage(new Message(MessageType.USERS, username, null, message));
            Logger.toConsole("[CONNECTION]", message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle new messages sent by the client
     */
    @Override
    public void run() {
        validateUsername();
        while (this.isConnected) {
            try {
                String receivedData = in.readLine();
                if (receivedData == null) {
                    throw new IOException(this.user.getUsername() + " disconnected");
                }

                if (!receivedData.equals("")) {
                    Message message = (Message) Serializer.toObject(receivedData);
                    //TODO: Handle different message types
                    if (message != null) {
                        switch (message.getType()) {
                            case CHAT:
                                Server.broadcastMessage(message);
                                break;
                            case CONNECTION:
                                break;
                            case DISCONNECTION:
                                break;
                            case INVALID_USERNAME:
                                break;
                            case USERS:
                                break;
                            case WHISPER:
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + message.getType());
                        }
                    } else {
                        Logger.toConsole("[ERROR]", "NULL MESSAGE");
                    }
                }
            } catch (IOException e) {
                disconnect();
                String message = String.format("%s has disconnected", this.user.getUsername());
                Server.broadcastMessage(new Message(MessageType.DISCONNECTION, null, null, message));
                Logger.toConsole("[DISCONNECTION]", message);
            }
        }
    }

    /**
     * Send messages in plaintext to the client
     *
     * @param message String: The message in plaintext
     */
    public synchronized void sendMessageAsPlaintext(String message) {
        this.out.write(message);
        this.out.flush();
    }

    /**
     * Send serialized messages to the client
     *
     * @param message Message: The message object to be serialized and sent to the client
     */
    public synchronized void sendMessage(Message message) {
        if (user.getUsername().contains("debug")) {
            sendMessageAsPlaintext(message.getBody());
        } else {
            String serializedMessage = Serializer.toBase64(message);
            this.out.write(serializedMessage);
            this.out.flush();
        }
    }

    /**
     * Handle disconnecting the client and removing their thread instance from the list of connected clients and their
     * username from the list of client usernames
     */
    public synchronized void disconnect() {
        try {
            this.clientSocket.close();
            this.in.close();
            this.out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Server.connectedClients.remove(this.user.getUsername());
            Server.clientUsernames.remove(this.user.getUsername());
            this.isConnected = false;
        }
    }
}
