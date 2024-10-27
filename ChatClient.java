import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

   
    public void start(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new MessageReceiver()).start();


        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String userMessage;
        while ((userMessage = consoleInput.readLine()) != null) {
            sendMessage(userMessage);
        }
    }


    public void sendMessage(String message) {
        out.println(message);
    }

    private class MessageReceiver implements Runnable {
        public void run() {
            String serverMessage;
            try {
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Server: " + serverMessage); 
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient();
        client.start("localhost", );  // Connect to  server running on localhost.... port 12345
    }
}

