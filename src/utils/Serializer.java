package utils;

import java.io.*;
import java.util.Base64;

public class Serializer {
    /**
     * Converts serializable objects to a byte array that is encoded in Base64
     * @param o The serializable object to toBase64
     * @return String: The Base64 representation of the byte array
     */
    public static String toBase64(Serializable o) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.close();

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts serialized strings to Message objects
     * @param serializedMessage The message bytes encoded in Base64
     * @return Object: the deserialized string as a message object
     */
    public static Object toObject(String serializedMessage) {
        try {
            byte[] bytes = Base64.getDecoder().decode(serializedMessage);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            objectInputStream.close();
            return objectInputStream.readObject();
        } catch (RuntimeException| IOException e) {
            System.out.println("Failed to deserialize message to a message object");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Message testMessage = new Message(MessageType.CHAT, "James", null, "Hello, World!");
        String data = toBase64(testMessage);
        System.out.println(data);
        System.out.println(toObject(data));
    }
}
