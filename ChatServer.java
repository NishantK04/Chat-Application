import java.io.*;
import java.net.*;
import java.util.*;

// Server class to handle multiple client connections
public class ChatServer {
    private ServerSocket serverSocket;      // Server socket to listen for incoming connections
    private List<ClientHandler> clients = new ArrayList<>();  // List of connected clients

    // Start the server on the specified port
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);

        // Continuously accept new client connections
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            // Handle the client in a separate thread
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clients.add(clientHandler);  // Add the client to the list of connected clients
            new Thread(clientHandler).start();  // Start the client handler in a new thread
        }
    }

    // Broadcast a message to all connected clients, except the sender
    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);  // Send the message to all clients except the sender
            }
        }
    }

    // Remove a client from the list when they disconnect
    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.start(12345);  // Start the server on port 12345
    }
}

// ClientHandler class to manage communication with a single client
class ClientHandler implements Runnable {
    private Socket clientSocket;        // The client socket
    private PrintWriter out;            // Output stream to send data to the client
    private BufferedReader in;          // Input stream to receive data from the client
    private ChatServer server;          // Reference to the ChatServer for message broadcasting

    // Constructor initializes client socket and server reference
    public ClientHandler(Socket socket, ChatServer server) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        out = new PrintWriter(clientSocket.getOutputStream(), true);   // Output stream to send data
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  // Input stream to receive data
    }

    // Run method to handle incoming messages from the client
    @Override
    public void run() {
        String message;
        try {
            // Continuously listen for messages from the client
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);  // Log received message
                server.broadcastMessage(message, this);      // Broadcast message to other clients
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();  // Close connection if an error occurs or client disconnects
        }
    }

    // Method to send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Close the client connection
    public void closeConnection() {
        try {
            in.close();           // Close input stream
            out.close();          // Close output stream
            clientSocket.close();  // Close the client socket
            server.removeClient(this);  // Remove the client from the server's list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
