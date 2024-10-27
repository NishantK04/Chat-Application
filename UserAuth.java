import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserAuth {
    private static final String USER_FILE = "users.txt";
    private Map<String, String> users = new HashMap<>();

    public UserAuth() {
        loadUsers();
    }

    // Load user credentials from the file
    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);  // Username, Password
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save user credentials to the file
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Register a new user
    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false;  // User already exists
        }
        users.put(username, password);
        saveUsers();
        return true;
    }

    // Log in an existing user
    public boolean login(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
