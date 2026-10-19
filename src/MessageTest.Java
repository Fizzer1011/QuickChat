import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testMessageLengthSuccess() {
        String result = Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", result);
    }

    @Test
    void testMessageLengthFailure() {
        String longText = "a".repeat(260);
        String result = Message.checkMessageLength(longText);
        assertTrue(result.contains("Message exceeds 250 characters by"));
    }

    @Test
    void testRecipientCorrect() {
        String result = Message.checkRecipientCell("+27718693002");
        assertEquals("Cell phone number successfully captured.", result);
    }

    @Test
    void testRecipientIncorrect() {
        String result = Message.checkRecipientCell("08575975889");
        assertTrue(result.contains("incorrectly formatted"));
    }

    @Test
    void testMessageHashTestCase1() {
        // Test data from brief: "Hi Mike, can you join us for dinner tonight?"
        Message msg = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.createMessageHash();
        // Expected style: 00:0:HITONIGHT
        assertTrue(hash.startsWith("00:0:"));
        assertTrue(hash.contains("HITONIGHT") || hash.contains("HI") && hash.contains("TONIGHT"));
    }

    @Test
    void testMessageIDLength() {
        Message msg = new Message("1234567890", 0, "+27718693002", "Hello");
        assertTrue(msg.checkMessageID());
    }
}
