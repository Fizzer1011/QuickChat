import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * PROG5121 - Complete PoE (Part 1 + Part 2 + Part 3)
 * Console application only.
 */
public class App {

    private String storedUsername;
    private String storedPassword;
    private String storedCell;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn = false;

    //Part 2 + Part 3
    private List<Message> sentMessages = new ArrayList<>();
    private List<Message> disregardedMessages = new ArrayList<>();
    private List<Message> storedMessages = new ArrayList<>();
    private List<String> messageHashes = new ArrayList<>();
    private List<String> messageIds = new ArrayList<>();

    private int totalMessagesSent = 0;

    //  Part 1 methods
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
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
        return cell != null && Pattern.matches("^\\+27[0-9]{9}$", cell);
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

    // Main menu (now with 4 options) 
    public void startMessaging(Scanner sc) {
        // Load any previously stored messages from JSON
        loadStoredMessagesFromFile();

        System.out.println("\nWelcome to QuickChat.");

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
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
                case "4":
                    storedMessagesMenu(sc);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Send Messages (Part 2) 
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

            String messageId = Message.generateMessageID();
            System.out.println("Message ID generated: " + messageId);

            System.out.print("Enter recipient cell number (+27...): ");
            String recipient = sc.nextLine().trim();
            String recipientCheck = Message.checkRecipientCell(recipient);
            System.out.println(recipientCheck);
            if (!recipientCheck.contains("successfully")) {
                System.out.println("Skipping this message.");
                continue;
            }

            System.out.print("Enter message (max 250 characters): ");
            String messageText = sc.nextLine().trim();
            String lengthCheck = Message.checkMessageLength(messageText);
            System.out.println(lengthCheck);
            if (!lengthCheck.contains("ready to send")) {
                continue;
            }

            Message msg = new Message(messageId, totalMessagesSent, recipient, messageText, firstName);
            String hash = msg.createMessageHash();
            System.out.println("Message Hash: " + hash);

            String actionResult = msg.SentMessage(sc);
            System.out.println(actionResult);

            if (actionResult.contains("successfully sent")) {
                sentMessages.add(msg);
                messageIds.add(messageId);
                messageHashes.add(hash);
                totalMessagesSent++;
            } else if (actionResult.contains("successfully stored")) {
                storedMessages.add(msg);
                messageIds.add(messageId);
                messageHashes.add(hash);
                msg.storeMessage();
            } else if (actionResult.contains("delete the message")) {
                disregardedMessages.add(msg);
            }
        }

        System.out.println("\nTotal messages sent in this session: " + totalMessagesSent);
        if (!sentMessages.isEmpty()) {
            System.out.println("\n--- Messages sent ---");
            for (Message m : sentMessages) {
                System.out.println(m.printMessages());
                System.out.println("-------------------");
            }
        }
    }

    // Part 3 – Stored Messages Menu 
    private void storedMessagesMenu(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Stored Messages Menu ===");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search messages by recipient");
            System.out.println("e) Delete a message using message hash");
            System.out.println("f) Display full report of all stored messages");
            System.out.println("g) Back to main menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    displaySenderAndRecipient();
                    break;
                case "b":
                    displayLongestMessage();
                    break;
                case "c":
                    System.out.print("Enter Message ID to search: ");
                    searchByMessageId(sc.nextLine().trim());
                    break;
                case "d":
                    System.out.print("Enter recipient number: ");
                    searchByRecipient(sc.nextLine().trim());
                    break;
                case "e":
                    System.out.print("Enter Message Hash to delete: ");
                    deleteByHash(sc.nextLine().trim());
                    break;
                case "f":
                    displayFullReport();
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // a) Display sender and recipient
    private void displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n--- Sender and Recipient ---");
        for (Message m : storedMessages) {
            System.out.println("Sender: " + m.getSender() + " | Recipient: " + m.getRecipient());
        }
    }

    // b) Display longest stored message
    private void displayLongestMessage() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        System.out.println("\nLongest stored message:");
        System.out.println(longest.getMessageText());
    }

    // c) Search by Message ID
    private void searchByMessageId(String id) {
        for (Message m : storedMessages) {
            if (m.getMessageId().equals(id)) {
                System.out.println("\nFound:");
                System.out.println("Recipient: " + m.getRecipient());
                System.out.println("Message: " + m.getMessageText());
                return;
            }
        }
        // Also check sent messages
        for (Message m : sentMessages) {
            if (m.getMessageId().equals(id)) {
                System.out.println("\nFound in sent messages:");
                System.out.println("Recipient: " + m.getRecipient());
                System.out.println("Message: " + m.getMessageText());
                return;
            }
        }
        System.out.println("Message ID not found.");
    }

    // d) Search by recipient
    private void searchByRecipient(String recipient) {
        boolean found = false;
        System.out.println("\nMessages for recipient " + recipient + ":");
        for (Message m : storedMessages) {
            if (m.getRecipient().equals(recipient)) {
                System.out.println("- " + m.getMessageText());
                found = true;
            }
        }
        for (Message m : sentMessages) {
            if (m.getRecipient().equals(recipient)) {
                System.out.println("- " + m.getMessageText());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages found for that recipient.");
        }
    }

    // e) Delete by hash
    private void deleteByHash(String hash) {
        Iterator<Message> it = storedMessages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.getMessageHash().equalsIgnoreCase(hash)) {
                System.out.println("Message: \"" + m.getMessageText() + "\" successfully deleted.");
                it.remove();
                messageHashes.remove(hash);
                messageIds.remove(m.getMessageId());
                // Also rewrite the JSON file
                rewriteStoredJson();
                return;
            }
        }
        System.out.println("Message hash not found.");
    }

    // f) Full report
    private void displayFullReport() {
        if (storedMessages.isEmpty() && sentMessages.isEmpty()) {
            System.out.println("No messages to report.");
            return;
        }
        System.out.println("\n========== FULL MESSAGE REPORT ==========");
        System.out.println("--- Stored Messages ---");
        for (Message m : storedMessages) {
            System.out.println(m.printMessages());
            System.out.println("----------------------------");
        }
        System.out.println("--- Sent Messages ---");
        for (Message m : sentMessages) {
            System.out.println(m.printMessages());
            System.out.println("----------------------------");
        }
    }

    // Load stored messages from the JSON file created in Part 2
    private void loadStoredMessagesFromFile() {
        File file = new File("stored_messages.json");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            // Very simple parser for the format we wrote in Part 2
            String[] objects = content.toString().split("\\},");
            for (String obj : objects) {
                if (!obj.contains("messageId")) continue;
                String id = extractValue(obj, "messageId");
                String hash = extractValue(obj, "messageHash");
                String recipient = extractValue(obj, "recipient");
                String text = extractValue(obj, "message");
                String sender = extractValue(obj, "sender");
                if (sender.isEmpty()) sender = "Unknown";

                Message m = new Message(id, 0, recipient, text, sender);
                m.setMessageHash(hash);
                storedMessages.add(m);
                messageIds.add(id);
                messageHashes.add(hash);
            }
        } catch (IOException e) {
            System.out.println("Could not load stored messages: " + e.getMessage());
        }
    }

    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return "";
        start = json.indexOf(":", start) + 1;
        start = json.indexOf("\"", start) + 1;
        int end = json.indexOf("\"", start);
        if (end == -1) return "";
        return json.substring(start, end);
    }

    private void rewriteStoredJson() {
        try (FileWriter writer = new FileWriter("stored_messages.json", false)) {
            for (Message m : storedMessages) {
                writer.write(m.toJson());
                writer.write(",\n");
            }
        } catch (IOException e) {
            System.out.println("Error rewriting JSON: " + e.getMessage());
        }
    }

    //  Main method 
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        App app = new App();

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

        app.startMessaging(sc);
        sc.close();
    }
}