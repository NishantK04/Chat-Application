import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClientGUI {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private String username; // Store the username

    // Start the GUI-based client
    public void start(String serverAddress, int port, String user) throws IOException {
        username = user; // Set the username
        // Setup the socket and input/output streams
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Initialize the GUI components
        frame = new JFrame("Chat Client - " + username);
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        messageField = new JTextField(40);

        // Add a button to send the message
        JButton sendButton = new JButton("Send");

        // Create a panel for the message field and send button
        JPanel messagePanel = new JPanel();
        messagePanel.add(messageField);
        messagePanel.add(sendButton);

        // Add components to the frame
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(messagePanel, BorderLayout.SOUTH);

        // Setup the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Handle sending a message
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        // Start a thread to receive messages from the server
        new Thread(new MessageReceiver()).start();
    }

    // Send the message typed by the user
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(username + ": " + message); // Include username with the message
            messageField.setText("");  // Clear the input field after sending
        }
    }

    // Thread to receive messages from the server and display them in the chat area
    private class MessageReceiver implements Runnable {
        public void run() {
            String serverMessage;
            try {
                while ((serverMessage = in.readLine()) != null) {
                    // Display the message in the chat area
                    chatArea.append(serverMessage + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method to handle user authentication
    public static void main(String[] args) throws IOException {
        UserAuth auth = new UserAuth();  // Initialize UserAuth
        boolean isAuthenticated = false;  // Flag to check authentication status
        String username = ""; // Store username for the client

        while (!isAuthenticated) {
            // Ask if the user wants to register or log in
            String[] options = {"Register", "Login"};
            int choice = JOptionPane.showOptionDialog(null, "Choose an option", "User Authentication",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            username = JOptionPane.showInputDialog("Enter username:");
            String password = JOptionPane.showInputDialog("Enter password:");

            // Handle registration
            if (choice == 0) { // Register
                if (auth.register(username, password)) {
                    JOptionPane.showMessageDialog(null, "Registration successful! Please log in.");
                } else {
                    JOptionPane.showMessageDialog(null, "Username already exists! Please try logging in.");
                }
            } else if (choice == 1) { // Login
                if (auth.login(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    isAuthenticated = true;  // User logged in successfully
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed! Please try again.");
                }
            }
        }

        // Start the chat client after successful authentication
        ChatClientGUI client = new ChatClientGUI();
        client.start("localhost", 12345, username);  // Connect to the server with username
    }
}
