package su.maibat.mon3tr.commands;

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
import su.maibat.mon3tr.commands.task.Postpone;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;
import su.maibat.mon3tr.notifier.Notifier;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public final class PostponeTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final Notifier notifier = Mockito.mock(Notifier.class);
    private final Postpone updateOffset = new Postpone(linker, notifier);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @BeforeEach
    void setUp() throws UserNotFound, DeadlineNotFound {
        UserQuery user = new UserQuery(1, 1234, new int[]{});
        Mockito.when(linker.getUserById(1)).thenReturn(user);

        long burnTime = 18082011;

        DeadlineQuery dl1 = new DeadlineQuery(1, "first", burnTime,
                0, 1, new int[]{}, false, 0);

        DeadlineQuery dl2 = new DeadlineQuery(2, "second", burnTime,
                0, 1, new int[]{}, false, 0);

        DeadlineQuery dl3 = new DeadlineQuery(3, "third", burnTime,
                0, 1, new int[]{}, false, 0);

        DeadlineQuery dl4 = new DeadlineQuery(4, "chotyri", burnTime,
                0, 1, new int[]{}, false, 0);


        DeadlineQuery[] deadlinesForUser = {dl1, dl2, dl3, dl4};

        Mockito.when(linker.getDeadlinesForUser(1)).thenReturn(deadlinesForUser);
        Mockito.when(linker.getDeadline(1)).thenReturn(dl1);
    }

    @ParameterizedTest(name = "Empty set")
    @MethodSource("emptyArgs")
    @DisplayName("Update without arguments")
    void withoutTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException, DeadlineNotFound {

        State state = new State(inStateId, new String[]{}, updateOffset);
        State resultState = updateOffset.execute(1, new String[]{}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);
            Mockito.verify(responseQueue, Mockito.times(2)).add(answerCaptor.capture());
        } else {
            Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());
        }
        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());

        assertEquals(outStateId, resultState.getStateId());
        assertEquals(state.getOwner(), resultState.getOwner());
    }

    static Stream<Arguments> emptyArgs() {
        return Stream.of(
                Arguments.of(0, 1, "Please enter a valid deadline id"),
                Arguments.of(1, 1, "Please enter a valid deadline id"),
                Arguments.of(2, 2, "Please enter a offset (days before final date)")
        );
    }

    @ParameterizedTest(name = "Args set")
    @MethodSource("someArgs")
    @DisplayName("Add with arguments")
    void withArgsTest(final int inStateId, final String answerText)
            throws CommandException, DeadlineNotFound {

        State state = new State(inStateId, new String[]{}, updateOffset);
        if (inStateId >= 1) {
            state.setMemory(new String[]{"1", "2", "3", "4"});
        }
        State resultState = updateOffset.execute(1, new String[]{"1"}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);
            Mockito.verify(responseQueue, Mockito.times(2)).add(answerCaptor.capture());
        } else {
            Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());
        }
        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answerText, answer.getString());

        if (inStateId == 2) {
            assertNull(resultState);
        } else {
            assertEquals(2, resultState.getStateId());
        }
    }

    static Stream<Arguments> someArgs() {
        return Stream.of(
                Arguments.of(0, "Please enter a offset (days before final date)"),
                Arguments.of(1, "Please enter a offset (days before final date)"),
                Arguments.of(2, "Task postponed")
        );
    }

    @Test
    @DisplayName("Empty deadline Test")
    void emptyDlTest() throws CommandException, DeadlineNotFound {
        DeadlineQuery[] queryList = new DeadlineQuery[]{};
        Mockito.when(linker.getDeadlinesForUser(1)).thenReturn(queryList);

        State resultState = updateOffset.execute(1, new String[]{"1"}, null,
                responseQueue);
        Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();

        assertEquals(1, answer.getNumber());
        assertEquals("You have no tasks", answer.getString());
        assertNull(resultState);
    }

    @ParameterizedTest(name = "Illegal Id Args set")
    @MethodSource("illegalIdArgs")
    @DisplayName("Add with Id arguments")
    void illegalIdTest(final String arg, final boolean valid)
            throws CommandException {

        State state = new State(1, new String[]{"1", "2", "3", "4"}, updateOffset);

        State resultState = updateOffset.execute(1, new String[]{arg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(valid, answer.getString().equals("Please enter a offset "
                + "(days before final date)"));
        assertEquals(valid, resultState.getStateId() == 2);
    }

    static Stream<Arguments> illegalIdArgs() {
        return Stream.of(
                Arguments.of("3", true),
                Arguments.of("0", false),
                Arguments.of("42", false)
        );
    }


    @ParameterizedTest(name = "Illegal Offset Args set")
    @MethodSource("illegalOffsetArgs")
    @DisplayName("Add with Offset arguments")
    void illegalOffsetTest(final String arg, final boolean valid)
            throws CommandException {

        State state = new State(2, new String[]{"1", "2", "3", "4"}, updateOffset);

        State resultState = updateOffset.execute(1, new String[]{arg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(valid, answer.getString().equals("Task postponed"));
        assertEquals(valid, resultState == null);
    }

    static Stream<Arguments> illegalOffsetArgs() {
        return Stream.of(
                Arguments.of("3", true),
                Arguments.of("-1", false),
                Arguments.of("42", true)
        );
    }
}
