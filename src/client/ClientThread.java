package client;

import utils.Message;
import utils.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;
    private boolean isConnected;

    public ClientThread(String hostname, int port, String username) {
        try {
            this.socket = new Socket(hostname, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            //TODO: Handle username validation
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send serialized messages to the client
     *
     * @param message Message: The message object to be serialized and sent to the client
     */
    public synchronized void sendMessage(Message message) {
        String serializedMessage = Serializer.toBase64(message);
        this.out.write(serializedMessage);
        this.out.flush();
    }

    @Override
    public void run() {
        // TODO: Message listening functionality
        while (this.isConnected) {
            continue;
        }
    }

    /**
     * Handle disconnecting the client and removing their thread instance from the list of connected clients and their
     * username from the list of client usernames
     */
    public synchronized void disconnect() {
        try {
            this.socket.close();
            this.in.close();
            this.out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.isConnected = false;
        }
    }
}
