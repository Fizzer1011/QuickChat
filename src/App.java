import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * PROG5121 - Part 1 + Part 2
 * Console application only.
 */
public class App {

    // ===== Part 1 fields =====
    private String storedUsername;
    private String storedPassword;
    private String storedCell;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn = false;

    // ===== Part 2 fields =====
    private List<Message> sentMessages = new ArrayList<>();
    private int totalMessagesSent = 0;

    // ---------- Part 1 validation methods ----------

    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    public boolean checkCellPhoneNumber(String cell) {
        if (cell == null) return false;
        // +27 followed by exactly 9 digits
        return Pattern.matches("^\\+27[0-9]{9}$", cell);
    }

    public String registerUser(String username, String password, String cell,
                               String firstName, String lastName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.storedUsername = username;
        this.storedPassword = password;
        this.storedCell = cell;
        this.firstName = firstName;
        this.lastName = lastName;

        return "Username successfully captured.\n"
             + "Password successfully captured.\n"
             + "Cell phone number successfully added.";
    }

    public boolean loginUser(String username, String password) {
        if (storedUsername == null || storedPassword == null) return false;
        isLoggedIn = storedUsername.equals(username) && storedPassword.equals(password);
        return isLoggedIn;
    }

    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // ---------- Part 2 main flow ----------

    public void startMessaging(Scanner sc) {
        System.out.println("\nWelcome to QuickChat.");

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    sendMessagesFlow(sc);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void sendMessagesFlow(Scanner sc) {
        System.out.print("How many messages do you wish to enter? ");
        int numMessages;
        try {
            numMessages = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Returning to menu.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            // Generate Message ID (10-digit random)
            String messageId = Message.generateMessageID();
            System.out.println("Message ID generated: " + messageId);

            // Recipient
            System.out.print("Enter recipient cell number (+27...): ");
            String recipient = sc.nextLine().trim();
            String recipientCheck = Message.checkRecipientCell(recipient);
            System.out.println(recipientCheck);
            if (!recipientCheck.contains("successfully")) {
                System.out.println("Skipping this message.");
                continue;
            }

            // Message text
            System.out.print("Enter message (max 250 characters): ");
            String messageText = sc.nextLine().trim();
            String lengthCheck = Message.checkMessageLength(messageText);
            System.out.println(lengthCheck);
            if (!lengthCheck.contains("ready to send")) {
                continue;
            }

            // Create Message object
            Message msg = new Message(messageId, totalMessagesSent, recipient, messageText);
            String hash = msg.createMessageHash();
            System.out.println("Message Hash: " + hash);

            // Ask user what to do
            String actionResult = msg.SentMessage(sc);
            System.out.println(actionResult);

            if (actionResult.contains("successfully sent")) {
                sentMessages.add(msg);
                totalMessagesSent++;
            } else if (actionResult.contains("successfully stored")) {
                msg.storeMessage(); // writes to JSON file
            }
            // disregard does nothing permanent
        }

        System.out.println("\nTotal messages sent in this session: " + totalMessagesSent);
        if (!sentMessages.isEmpty()) {
            System.out.println("\n--- Messages sent ---");
            for (Message m : sentMessages) {
                System.out.println(m.printMessages());
            }
        }
    }

    // ---------- Main method ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        App app = new App();

        // ===== Registration =====
        System.out.println("=== QuickChat Registration ===");
        System.out.print("Enter first name: ");
        String firstName = sc.nextLine().trim();
        System.out.print("Enter last name: ");
        String lastName = sc.nextLine().trim();
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        System.out.print("Enter SA cell phone number (e.g. +27838968976): ");
        String cell = sc.nextLine().trim();

        String regResult = app.registerUser(username, password, cell, firstName, lastName);
        System.out.println(regResult);

        if (!regResult.contains("successfully")) {
            System.out.println("Registration failed. Exiting.");
            sc.close();
            return;
        }

        // ===== Login =====
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String loginUser = sc.nextLine().trim();
        System.out.print("Password: ");
        String loginPass = sc.nextLine().trim();

        String loginMsg = app.returnLoginStatus(loginUser, loginPass);
        System.out.println(loginMsg);

        if (!app.isLoggedIn()) {
            System.out.println("Login failed. Exiting.");
            sc.close();
            return;
        }

        // ===== Part 2 messaging menu =====
        app.startMessaging(sc);

        sc.close();
    }
}
