package su.maibat.mon3tr.chat;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.generics.TelegramClient;


class TelegramChatTest {
    private TelegramClient tgClient;
    private TelegramChat chat;

    @BeforeEach
    void setUp() {
        tgClient = Mockito.mock(TelegramClient.class);
        chat = new TelegramChat(123L, tgClient);
    }

    @Test
    @DisplayName("Base test")
    void baseTest() throws InterruptedException {
        assertEquals(123L, chat.getChatId(), "Passed id and fetched should match");
        assertTrue(chat.isEmpty(), "Inner buffer should be empty");

        String testMessage = "Test message";
        chat.addMessage(testMessage);
        assertTrue(!chat.isEmpty(), "Should not be empty");
        assertEquals(testMessage, chat.getMessage(), "Value should not change");
    }

    @Test
    @DisplayName("All messages test")
    void getAllMessagesTest() {
        String[] testMessages = {"testMessage", "testMessage + testMessage",
            "More", "and more"};

        chat.addMessage(testMessages[0]);
        chat.addMessage(testMessages[1]);
        chat.addMessages(Arrays.copyOfRange(testMessages, 2, testMessages.length));

        String[] answer = chat.getAllMessages();
        assertTrue(chat.isEmpty());
        assertEquals(testMessages.length, answer.length);

        for (int i = 0; i < testMessages.length; i++) {
            assertEquals(testMessages[i], answer[i], "Initial messages should be equal resulting");
        }
    }

    @Test
    @DisplayName("Frozing test")
    void frozingTest() throws InterruptedException {
        assertTrue(!chat.isFrozen(), "Should not be frozen after initialization");
        chat.freeze();
        assertTrue(chat.isFrozen(), "Should froze if told to");
        assertDoesNotThrow(() -> chat.unfreeze(), "Should not throw on empty inners");
        assertTrue(!chat.isFrozen(), "Should unfroze if told to");

        String[] testMessages = {"testMessage", "testMessage + testMessage",
            "More", "and more"};
        chat.addMessages(testMessages);
        chat.freeze();
        chat.addMessage("This message should not be displayed");
        String[] result = chat.getAllMessages();
        assertEquals(testMessages.length, result.length);
        assertTrue(!chat.isFrozen(), "Should unfroze automatically");
        assertTrue(!chat.isEmpty(), "Should have left one message");
        assertEquals("This message should not be displayed", chat.getMessage());
        assertTrue(chat.isEmpty(), "Finally, should not have anything");

        chat.freeze();
        chat.addMessages(testMessages);
        assertTrue(!chat.isEmpty());
        chat.getMessage();
        assertTrue(!chat.isFrozen());
    }

    @Test
    @DisplayName("Interruption test")
    void interruptionTest() throws InterruptedException {
        class Wrapper implements Runnable {
            private final TelegramChat chat;
            Wrapper(final TelegramChat chatArg) {
                chat = chatArg;
            }

            public void run() {
                try {
                    chat.getMessage();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted");
                }
            }
        }

        Thread thread = new Thread(new Wrapper(chat));
        thread.start();
        chat.interrupt();
        thread.join(500);
        assertTrue(!thread.isAlive(), "Should die");
    }
}
