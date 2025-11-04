package su.maibat.mon3tr.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AddCommandTest {
    private final Chat chat = Mockito.mock(Chat.class);
    private final DataBaseLinker linker = Mockito.mock(SQLiteLinker.class);
    private final DeadlineAddCommand add = new DeadlineAddCommand(linker);


    @BeforeEach
    void setUp() throws UserNotFound {
        long chatId = 1234;
        Mockito.when(chat.getChatId()).thenReturn(chatId);

        UserQuery user = new UserQuery(1, 1234);
        Mockito.when(linker.getUserByChatId(1234)).thenReturn(user);
    }

    @Test
    @DisplayName("Add without arguments")
    void incorrectAddTest() {
        assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Something went wrong, try again with input some arguments", answer);
    }

    @Test
    @DisplayName("Add with correct arguments")
    void testWithArgs(){
        String[] data = {"Deadline", "20.12.2025"};
        Mockito.when(chat.getAllMessages()).thenReturn(data);

        assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        Mockito.verify(add, Mockito.times(1)).isDate("20.12.2025");

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Deadline added successfully", answer);
    }

    @Test
    @DisplayName("Add with reverse order of correct arguments")
    void testWithReverseArgs(){
        String[] data = {"20.12.2025", "Deadline"};
        Mockito.when(chat.getAllMessages()).thenReturn(data);

        assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        Mockito.verify(add, Mockito.times(1)).isDate("20.12.2025");

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        assertEquals("Deadline added successfully", answer);
    }


    @ParameterizedTest(name = "Date set")
    @MethodSource("dateArgs")
    @DisplayName("Add with incorrect date")
    void incorrectDateTest(String[] args, Boolean[] isValid) { //Возможен второй аргумент
        assertEquals(args.length, isValid.length, "MALFORMED TEST DATA");
        for (int i = 0; i < args.length; i++) {
            String[] data = {"SecondDeadline", args[i]};
            Mockito.when(chat.getAllMessages()).thenReturn(data);

            assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

            ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

            assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
            String answer = answerCaptor.getValue();

            assertEquals(!isValid[i], answer.contains("Please enter correct date"),
                    "Date " + args[i] + "is not valid");

        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> dateArgs() {
        return Stream.of(
            Arguments.of(new String[]{"20.12.2025","20/12/2025", "20;12;2025"},
                    new Boolean[]{true, true, false}),
            Arguments.of(new String[]{"20.12.2025", "31.02.2025", "0.12.2025"},
                    new Boolean[]{true, true, true}),
            Arguments.of(new String[]{"20.12.2025", "20.15.2025", "20.0.2025"},
                    new Boolean[]{true, true, true}),
            Arguments.of(new String[]{"20.12.2025", "20.12", "20.12."},
                    new Boolean[]{true, false, false}),
            Arguments.of(new String[]{"31.12.2025", "abracadabra", ""},
                        new Boolean[]{true, false, false})
        );
    }

    @Test
    @DisplayName("Add with incorrect deadline name")
    void incorrectNameTest() {
        String[] arguments = {"deadline", "Al6ed0_was_here", "", "17.08.2000"};
        Boolean[] isValid = {true, true, false, false};

        for (int i = 0; i < arguments.length; i++) {
            String[] data = {arguments[i], "20.12.2025"};
            Mockito.when(chat.getAllMessages()).thenReturn(data);

            assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

            ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

            assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
            String answer = answerCaptor.getValue();

            assertEquals(!isValid[i], answer.contains("Please enter valid name"),
                    "Name " + arguments[i] + "is not valid");
        }
    }




}
