# Multi-Threaded TCP Chat Application

This project is a **multi-threaded TCP chat application** built in Java, leveraging the **Sockets API** for client-server communication and **Java Swing** for the graphical user interface (GUI). It was developed as a group project for the CS313 networking course.

### **Project Overview**  
The application uses a client-server architecture where the server manages multiple client connections concurrently through multi-threading. Each client can send and receive messages in real-time via a TCP connection. The chat system is enhanced with a GUI created using Java Swing, providing users with an intuitive interface to interact with the application.

### **Key Features:**
- **Multi-threaded server**: Efficiently manages concurrent client connections, ensuring smooth real-time messaging.
- **Reliable communication**: Utilizes the Sockets API with TCP for robust and dependable message transfer.
- **Interactive GUI**: Java Swing provides a clean and user-friendly interface for chat interactions.
- **Real-time messaging**: Facilitates immediate message exchange between connected clients.

This project demonstrates essential concepts in networking, multi-threading, and GUI development, showcasing how these technologies can be integrated to create a functional and interactive application.

### Compiling the project
```shell
cd src/main/java
chmod +x build.sh
./build.sh
```

### Running the server
```shell
# In src/main/java
# If not: cd src/main/java
java TCPChatApp server <IP Address> <Port>
```

### Running the client
```shell
# In src/main/java
# If not: cd src/main/java
java TCPChatApp client
```
 
**USEFUL RESOURCES:**

[Notion Document](https://www.notion.so/invite/d1cb5828b9464fec174f77872cd84ad634178656)

[Project Specification](https://cs354.cs.sun.ac.za/ASSIGNMENTS/Assignment1.pdf)

[Chat Server Design Documentation](https://lisas.de/~hauser/download/chatserver.pdf)

[RW354 Project 1 implementation example](https://github.com/ArnoldVssr/VoIP)

---
