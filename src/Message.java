import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Message class required by Part 2 of PROG5121.
 */
public class Message {

    private String messageId;
    private int messageNumber;   // auto-incremented counter
    private String recipient;
    private String messageText;
    private String messageHash;

    public Message(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // ---------- Required methods ----------

    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    public static String checkRecipientCell(String cell) {
        if (cell == null) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        // Re-use same rule as Part 1
        if (Pattern.matches("^\\+27[0-9]{9}$", cell)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public static String checkMessageLength(String text) {
        if (text == null) text = "";
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        int over = text.length() - 250;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    public String createMessageHash() {
        // Format: first two digits of ID : message number : FIRSTLAST (all caps)
        String firstTwo = (messageId != null && messageId.length() >= 2)
                ? messageId.substring(0, 2) : "00";

        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord  = words.length > 1 ? words[words.length - 1] : firstWord;

        // Remove any non-letter characters for cleanliness
        firstWord = firstWord.replaceAll("[^a-zA-Z]", "");
        lastWord  = lastWord.replaceAll("[^a-zA-Z]", "");

        messageHash = (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    public String SentMessage(Scanner sc) {
        System.out.println("\nWhat would you like to do with this message?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter choice: ");

        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                return "Message successfully sent.";
            case "2":
                return "Press 0 to delete the message.";
            case "3":
                return "Message successfully stored.";
            default:
                return "Invalid choice – message disregarded.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageId
             + "\nMessage Hash: " + messageHash
             + "\nRecipient: " + recipient
             + "\nMessage: " + messageText;
    }

    public int returnTotalMessagess() {
        return messageNumber + 1; // just returns current count context
    }

    /**
     * Stores the message as a simple JSON object in a file.
     * Attribution: basic file writing approach (no external library required).
     */
    public void storeMessage() {
        String json = "{\n"
                + "  \"messageId\": \"" + messageId + "\",\n"
                + "  \"messageHash\": \"" + messageHash + "\",\n"
                + "  \"recipient\": \"" + recipient + "\",\n"
                + "  \"message\": \"" + messageText.replace("\"", "\\\"") + "\"\n"
                + "}\n";

        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            writer.write(json);
            writer.write(",\n"); // simple separator for multiple entries
            System.out.println("Message written to stored_messages.json");
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // ---------- Helper ----------
    public static String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    // Getters used by tests / later parts
    public String getMessageId() { return messageId; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
}
