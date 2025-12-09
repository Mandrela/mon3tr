package su.maibat.mon3tr.commands;


import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;
import su.maibat.mon3tr.notifier.Notifier;

public final class AddCommandTest {
    private final DataBaseLinker linker = Mockito.mock(SQLiteLinker.class);
    private final Notifier notifier = Mockito.mock(Notifier.class);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);
    private final DeadlineAddCommand add = new DeadlineAddCommand(linker, notifier);


    @BeforeEach
    void setUp() throws UserNotFound {
        long chatId = 1234;
        int[] groups = new int[]{};

        UserQuery user = new UserQuery(1, chatId, groups);
        user.setLimit(32);
        Mockito.when(linker.getUserById(1)).thenReturn(user);
    }

    @ParameterizedTest(name = "Empty set")
    @MethodSource("emptyArgs")
    @DisplayName("Add without arguments")
    void withoutTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException {

        State state = new State(inStateId, new String[]{}, add);
        State resultState = add.execute(1, new String[]{}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());

        assertEquals(outStateId, resultState.getStateId());
        assertEquals(state.getOwner(), resultState.getOwner());
        assertEquals(state.getMemory(), resultState.getMemory());

    }

    static Stream<Arguments> emptyArgs() {
        return Stream.of(
                Arguments.of(0, 0, "Please, enter a valid name"),
                Arguments.of(1, 1, "Please, enter a valid date")
        );
    }

    @ParameterizedTest(name = "Name set")
    @MethodSource("nameArgs")
    @DisplayName("Add with name-argument")
    void nameArgTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException {

        State state = new State(inStateId, new String[]{}, add);
        if (inStateId == 1) {
            state.setMemory(new String[]{"Paradigmatic", ""});
        }
        State resultState = add.execute(1, new String[]{"Paradigmatic"}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());

        assertEquals(outStateId, resultState.getStateId());
        assertEquals(state.getOwner(), resultState.getOwner());
        assertEquals("Paradigmatic", resultState.getMemory()[0]);
        assertEquals("", resultState.getMemory()[1]);

    }

    static Stream<Arguments> nameArgs() {
        return Stream.of(
                Arguments.of(0, 1, "Please, enter a valid date"),
                Arguments.of(1, 1, "Please, enter a valid date")
        );
    }

    @ParameterizedTest(name = "Date set")
    @MethodSource("dateArgs")
    @DisplayName("Add with date-argument")
    void dateArgTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException {

        State state = new State(inStateId, new String[]{}, add);
        if (inStateId == 1) {
            state.setMemory(new String[]{"Paradigmatic", ""});
        }
        State resultState = add.execute(1, new String[]{"24.12.25"}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());
        if (inStateId == 1) {
            assertNull(resultState);
        } else {
            assertEquals(outStateId, resultState.getStateId());
            assertEquals(state.getOwner(), resultState.getOwner());
            assertEquals(state.getMemory(), resultState.getMemory());
        }
    }

    static Stream<Arguments> dateArgs() {
        return Stream.of(
                Arguments.of(0, 0, "Please, enter a valid name"),
                Arguments.of(1, -1, "Deadline added successfully")
        );
    }

    @ParameterizedTest(name = "Second state set")
    @MethodSource("secondStateArgs")
    @DisplayName("2 state with args")
    void secondStateTest(final String[] args, final  String answerText)
            throws CommandException {

        State state = new State(2, new String[]{"Paradigmatic", "25.12.25"}, add);

        State resultState = add.execute(1, args, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());

        assertNull(resultState);
    }

    static Stream<Arguments> secondStateArgs() {
        return Stream.of(
                Arguments.of(new String[]{}, "Deadline added successfully"),
                Arguments.of(new String[]{"Paradigmatic"}, "Deadline added successfully"),
                Arguments.of(new String[]{"25.12.25"}, "Deadline added successfully")
        );
    }

    @ParameterizedTest(name = "Different names set")
    @MethodSource("incorrectNameArgs")
    @DisplayName("Add with different name-args")
    void differentNamesTest(final String nameArg, final boolean valid)
            throws CommandException {

        State state = new State(0, new String[]{}, add);

        State resultState = add.execute(1, new String[]{nameArg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answer.getString().equals("Please, enter a valid date"), valid);
        assertEquals(resultState.getStateId() == 1, valid);
    }

    static Stream<Arguments> incorrectNameArgs() {
        return Stream.of(
                Arguments.of("Paradigmatic", true),
                Arguments.of("27.12.25", false),
                Arguments.of("", false)
        );
    }

    @ParameterizedTest(name = "Different dates set")
    @MethodSource("incorrectDateArgs")
    @DisplayName("Add with different Date-args")
    void differentDatesTest(final String dateArg, final boolean valid)
            throws CommandException {

        State state = new State(1, new String[]{"Paradigmatic", ""}, add);

        State resultState = add.execute(1, new String[]{dateArg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(valid, answer.getString().equals("Deadline added successfully"));
        assertEquals(valid, resultState == null);
    }

    static Stream<Arguments> incorrectDateArgs() {
        return Stream.of(
                Arguments.of("27.12.25", true),
                Arguments.of("27/12/25", true),
                Arguments.of("27:12:25", false),
                Arguments.of("76/12/25", true),
                Arguments.of("27/56/25", true),
                Arguments.of("-1/12/25", false),
                Arguments.of("27/0/25", true),
                Arguments.of("Silence", false),
                Arguments.of("", false)
        );
    }

    @Test
    @DisplayName("End-to-end Test")
    void endToEndTest() throws CommandException {
        State resultState = add.execute(1, new String[]{"Paradigmatic", "24.12.25"}, null,
                responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals("Deadline added successfully", answer.getString());
        assertNull(resultState);
    }

    @Test
    @DisplayName("Limit ends Test")
    void overLimitTest() throws CommandException, UserNotFound {
        UserQuery user = new UserQuery(1, 1234, new int[]{});
        user.setLimit(0);
        Mockito.when(linker.getUserById(1)).thenReturn(user);

        State resultState = add.execute(1, new String[]{"Paradigmatic", "24.12.25"}, null,
                responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals("You have used up all your deadline cells, please close one or "
                + "more deadlines before add a new one.", answer.getString());
        assertNull(resultState);
    }


}

