package su.maibat.mon3tr.telegramwrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public final class ResponderTest {
    private TelegramClient tg;
    private DataBaseLinker db;
    private BlockingQueue<NumberedString> queue;
    private HashMap<Integer, Long> tempIdMap;
    private Thread responderThread;
    private ArgumentCaptor<SendMessage> captor;


    void stopThread() throws InterruptedException {
        responderThread.interrupt();
        responderThread.join(5000);
        if (responderThread.isAlive()) {
            throw new RuntimeException("Responder Thread didn't dieee");
        }
    }


    @BeforeEach
    void mockInit() throws Exception {
        tg = Mockito.mock(TelegramClient.class);
        db = Mockito.mock(DataBaseLinker.class);
        queue = new ArrayBlockingQueue<>(8);
        tempIdMap = new HashMap<>();
        captor = ArgumentCaptor.forClass(SendMessage.class);

        Responder responder = new Responder(tg, queue, db, tempIdMap);
        responderThread = new Thread(responder);
        responderThread.setDaemon(true);

        Mockito.when(db.getChatIdByUserId(1)).thenReturn(1L);
        Mockito.when(db.getChatIdByUserId(2)).thenThrow(UserNotFound.class);
    }

    @Test
    @DisplayName("Init test")
    void initializationTest() {
        assertThrows(IllegalArgumentException.class, () -> new Responder(tg, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Responder(null, queue, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Responder(null, null, db, null));
        assertThrows(IllegalArgumentException.class, () -> new Responder(null, null, null, null));
        new Responder(tg, queue, db, null); // shouldn't throw
    }

    @Test
    @DisplayName("Standard Behaviour")
    void standardBehaviourTest() throws Exception {
        // Running on fucked queue + checking for work completion mechanism
        queue.add(new NumberedString(1, "test"));
        queue.add(new NumberedString(2, "kek"));

        responderThread.start();
        stopThread();


        assertTrue(queue.isEmpty(), "Should've completed work");

        Mockito.verify(tg, Mockito.times(1)).execute(captor.capture());

        assertEquals("test", captor.getValue().getText());
    }


    @Test
    @DisplayName("Telegram troubles simulation")
    void tgTroubleTest() throws Exception {
        responderThread.start();
        Thread.yield(); // let it work on empty queue
        System.out.println("THISS FUCKING UNIQUE TEST");
        Mockito.when(tg.execute(Mockito.any(SendMessage.class))).
            thenThrow(TelegramApiException.class).thenThrow(TelegramApiException.class).
            thenReturn(null);
        queue.add(new NumberedString(1, "test"));
        stopThread();

        // Should try to resend.
        Mockito.verify(db, Mockito.times(1)).getChatIdByUserId(1);
        Mockito.verify(tg, Mockito.atLeast(3)).execute(captor.capture());
        captor.getAllValues();
    }

    @Test
    @DisplayName("Telegram is not working")
    void tgNotWorkingTest() throws Exception {
        Mockito.when(tg.execute(Mockito.any(SendMessage.class))).
            thenThrow(TelegramApiException.class);
        queue.add(new NumberedString(1, "test"));

        responderThread.start();
        responderThread.join(5000); // Should die
        if (responderThread.isAlive()) {
            throw new RuntimeException("Thread didn't die on telegram not working");
        }
    }


    @Test
    @DisplayName("Id remapping test")
    void idRemapTest() throws Exception {
        // remapping uid 1 to chatId 2, by deafult db returns 1 to 1 mapping
        tempIdMap.put(1, 2L);
        queue.add(new NumberedString(1, "kek"));
        responderThread.start();

        Mockito.verify(tg, Mockito.timeout(1000).times(1)).execute(captor.capture());
        assertEquals("2", captor.getValue().getChatId(), "Should prefer temp ids over real");

        tempIdMap.remove(1);
        queue.add(new NumberedString(1, "lol"));
        stopThread();

        Mockito.verify(tg, Mockito.times(2)).execute(captor.capture());
        assertEquals("1", captor.getValue().getChatId(), "Should not cache values");
    }
}
