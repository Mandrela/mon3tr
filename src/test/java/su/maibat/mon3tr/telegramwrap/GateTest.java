package su.maibat.mon3tr.telegramwrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.WeakHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import su.maibat.mon3tr.bot.BotBackend;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public final class GateTest {
    // Telegram commands to bot commands
    // temp Uid are unique for different users
    private BotBackend bot;
    private DataBaseLinker db;
    private WeakHashMap<Integer, Long> tempIdMap;
    private Gate gate;

    private Update update;
    private Message message;
    private ArgumentCaptor<Integer> idCaptor;
    private ArgumentCaptor<String> commandCaptor;


    @BeforeEach
    void mockInit() {
        bot = Mockito.mock(BotBackend.class);
        db = Mockito.mock(DataBaseLinker.class);
        tempIdMap = new WeakHashMap<>();

        update = Mockito.mock(Update.class);
        message = Mockito.mock(Message.class);

        gate = new Gate(bot, db, tempIdMap);


        Mockito.when(bot.getCommandPrefix()).thenReturn('&');

        Mockito.when(update.hasMessage()).thenReturn(true);
        Mockito.when(update.getMessage()).thenReturn(message);

        Mockito.when(message.hasText()).thenReturn(true);
        Mockito.when(message.getChatId()).thenReturn(1L).thenReturn(2L);
        Mockito.when(message.getText()).thenReturn("/command");


        idCaptor = ArgumentCaptor.forClass(Integer.class);
        commandCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    @DisplayName("Init test")
    void initializationTest() {
        assertThrows(IllegalArgumentException.class, () -> new Gate(bot, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Gate(null, db, null));
        assertThrows(IllegalArgumentException.class, () -> new Gate(null, null, tempIdMap));
        assertThrows(IllegalArgumentException.class, () -> new Gate(null, null, null));
    }


    @Test
    @DisplayName("Command transformation")
    void commandTransformTest() throws Exception {
        Mockito.when(db.getUserIdByChatId(1L)).thenReturn(1);

        gate.consume(update);

        Mockito.verify(bot, Mockito.times(1)).process(idCaptor.capture(), commandCaptor.capture());
        assertEquals(1, idCaptor.getValue(), "Shouldn't transform knownd ids");
        assertEquals("&command", commandCaptor.getValue(), "Should transform commands correctly");
    }


    @Test
    @DisplayName("Unique id for different users")
    void uniqueIdTest() throws Exception {
        Mockito.when(db.getUserIdByChatId(Mockito.anyLong())).thenThrow(UserNotFound.class);

        gate.consume(update);
        gate.consume(update);

        Mockito.verify(bot, Mockito.times(2)).process(idCaptor.capture(), Mockito.any());

        assertTrue(idCaptor.getAllValues().get(0) != idCaptor.getAllValues().get(1));
    }
}
