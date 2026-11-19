import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MessageTest {

    private List<Message> sent;
    private List<Message> stored;
    private List<Message> disregarded;

    @BeforeEach
    void setUp() {
        sent = new ArrayList<>();
        stored = new ArrayList<>();
        disregarded = new ArrayList<>();

        // Official test data from the brief
        Message m1 = new Message("1000000001", 0, "+27834557896",
                "Did you get the cake?", "Developer");
        Message m2 = new Message("1000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "Developer");
        Message m3 = new Message("1000000003", 2, "+27834484567",
                "Yohoooo, I am at your gate.", "Developer");
        Message m4 = new Message("1000000004", 3, "0838884567",
                "It is dinner time !", "Developer");
        Message m5 = new Message("1000000005", 4, "+27838884567",
                "Ok, I am leaving without you.", "Developer");

        sent.add(m1);
        sent.add(m4);
        stored.add(m2);
        stored.add(m5);
        disregarded.add(m3);
    }

    @Test
    void testSentMessagesPopulated() {
        assertEquals(2, sent.size());
        assertEquals("Did you get the cake?", sent.get(0).getMessageText());
        assertEquals("It is dinner time !", sent.get(1).getMessageText());
    }

    @Test
    void testLongestMessage() {
        Message longest = stored.get(0);
        for (Message m : stored) {
            if (m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        assertTrue(longest.getMessageText().contains("Where are you? You are late!"));
    }

    @Test
    void testSearchByRecipient() {
        List<String> results = new ArrayList<>();
        for (Message m : stored) {
            if (m.getRecipient().equals("+27838884567")) {
                results.add(m.getMessageText());
            }
        }
        assertEquals(2, results.size());
    }

    @Test
    void testDeleteByHash() {
        String hashToDelete = stored.get(0).getMessageHash();
        stored.removeIf(m -> m.getMessageHash().equals(hashToDelete));
        assertEquals(1, stored.size());
    }

    @Test
    void testMessageHashFormat() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", "Test");
        String hash = m.createMessageHash();
        assertTrue(hash.startsWith("00:0:"));
        assertTrue(hash.contains("HITONIGHT") || (hash.contains("HI") && hash.contains("TONIGHT")));
    }
}