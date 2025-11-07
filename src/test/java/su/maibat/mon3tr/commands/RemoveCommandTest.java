package su.maibat.mon3tr.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveCommandTest {
    private final Chat chat = Mockito.mock(Chat.class);
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final DeadlineRemoveCommand remove = new DeadlineRemoveCommand(linker);

    //Все аргументы верные
    //Неверный id (нет такого дедлайна)
    //Неверный id (нет такого дедлайна у данного пользователя)
    //Неверный пользователь (нет пользователя) - X
    //Неверный ввод
    //Отсутствие аргументов

    @BeforeEach
    void setUp() throws UserNotFound {
        long chatId = 1234;
        Mockito.when(chat.getChatId()).thenReturn(chatId);

        UserQuery user = new UserQuery(1, 1234);
        Mockito.when(linker.getUserByChatId(1234)).thenReturn(user);
        Mockito.when(linker.getUserById(1)).thenReturn(user);

        String[] data = {"4"};
        Mockito.when(chat.getAllMessages()).thenReturn(data);
    }


    @Test
    @DisplayName("Remove with correct arguments")
    void correctRemoveTest() throws UserNotFound, DeadlineNotFound {

        DeadlineQuery deadline = new DeadlineQuery();
        deadline.setUserId(1);

        Mockito.when(linker.getDeadline(4)).thenReturn(deadline);

        assertDoesNotThrow(() -> remove.execute(chat));

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();
        Mockito.verify(linker, Mockito.times(1)).getDeadline(4);
        Mockito.verify(linker, Mockito.times(1)).getUserById(1);
        Mockito.verify(chat, Mockito.times(1)).getChatId();

        Mockito.verify(linker, Mockito.times(1)).removeDeadline(4);

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("You have closed this gestalt!!!", answer);

    }

    @Test
    @DisplayName("Remove non-existent deadline")
    void incorrectIdTest() throws DeadlineNotFound {
        Mockito.when(linker.getDeadline(4)).thenThrow(DeadlineNotFound.class);

        assertDoesNotThrow(() -> remove.execute(chat));

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();
        Mockito.verify(linker, Mockito.times(1)).getDeadline(4);
        Mockito.verify(chat, Mockito.never()).getChatId();

        Mockito.verify(linker, Mockito.times(1)).getDeadline(4);

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Deadline not found", answer);
    }


    @Test
    @DisplayName("Remove someone else's deadline")
    void userDoNotHaveThisDeadlineTest() throws UserNotFound, DeadlineNotFound {
        DeadlineQuery deadline = new DeadlineQuery();
        deadline.setUserId(4);

        Mockito.when(linker.getDeadline(4)).thenReturn(deadline);

        UserQuery user2 = new UserQuery(4, 1235);
        Mockito.when(linker.getUserById(4)).thenReturn(user2);

        assertDoesNotThrow(() -> remove.execute(chat));

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();
        Mockito.verify(linker, Mockito.times(1)).getDeadline(4);
        Mockito.verify(linker, Mockito.times(1)).getUserById(4);
        Mockito.verify(chat, Mockito.times(1)).getChatId();

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("You do not have this deadline " +
                "(do not take on more than you need to)", answer);
    }

    @Test
    @DisplayName("Remove with illegal arguments")
    void illegalArgumentsTest() {
        String[] data = {"abracadabra"};
        Mockito.when(chat.getAllMessages()).thenReturn(data);

        assertDoesNotThrow(() -> remove.execute(chat));
        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Please enter a valid deadline id (number)", answer);
    }

    @Test
    @DisplayName("Remove without arguments")
    void emptyArgumentsTest() {
        String[] data = {};
        Mockito.when(chat.getAllMessages()).thenReturn(data);

        assertDoesNotThrow(() -> remove.execute(chat));
        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Something went wrong, try again with input some arguments", answer);
    }


}
