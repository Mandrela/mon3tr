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
import su.maibat.mon3tr.commands.task.RemoveTask;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public final class RemoveTaskTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final RemoveTask remove = new RemoveTask(linker);
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
    }

    @ParameterizedTest(name = "Empty set")
    @MethodSource("emptyArgs")
    @DisplayName("Add without arguments")
    void withoutTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException, DeadlineNotFound {

        State state = new State(inStateId, new String[]{}, remove);
        State resultState = remove.execute(1, new String[]{}, state, responseQueue);

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
                Arguments.of(0, 1, "Please enter a valid task id"),
                Arguments.of(1, 1, "Please enter a valid task id")
        );
    }

    @ParameterizedTest(name = "Args set")
    @MethodSource("someArgs")
    @DisplayName("Add with arguments")
    void withArgsTest(final int inStateId, final String answerText)
            throws CommandException, DeadlineNotFound {

        State state = new State(inStateId, new String[]{}, remove);
        if (inStateId == 1) {
            state.setMemory(new String[]{"1", "2", "3", "4"});
        }
        State resultState = remove.execute(1, new String[]{"1"}, state, responseQueue);

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

        assertNull(resultState);
    }

    static Stream<Arguments> someArgs() {
        return Stream.of(
                Arguments.of(0, "Task removed"),
                Arguments.of(1, "Task removed")
        );
    }

    @Test
    @DisplayName("Empty deadline Test")
    void emptyDlTest() throws CommandException, DeadlineNotFound {
        DeadlineQuery[] queryList = new DeadlineQuery[]{};
        Mockito.when(linker.getDeadlinesForUser(1)).thenReturn(queryList);

        State resultState = remove.execute(1, new String[]{"1"}, null,
                responseQueue);
        Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();

        assertEquals(1, answer.getNumber());
        assertEquals("You have no tasks", answer.getString());
        assertNull(resultState);
    }

    @ParameterizedTest(name = "Illegal Args set")
    @MethodSource("illegalArgs")
    @DisplayName("Add with arguments")
    void illegalArgsTest(final String arg, final boolean valid)
            throws CommandException {

        State state = new State(1, new String[]{"1", "2", "3", "4"}, remove);

        State resultState = remove.execute(1, new String[]{arg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(valid, answer.getString().equals("Task removed"));
        assertEquals(valid, resultState == null);
    }

    static Stream<Arguments> illegalArgs() {
        return Stream.of(
                Arguments.of("3", true),
                Arguments.of("0", false),
                Arguments.of("42", false)
        );
    }
}
