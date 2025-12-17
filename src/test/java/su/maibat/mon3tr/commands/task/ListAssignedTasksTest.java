package su.maibat.mon3tr.commands.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public final class ListAssignedTasksTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final ListAssignedTasks list = new ListAssignedTasks(linker);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @BeforeEach
    void setUp() throws UserNotFound, GroupNotFound {

        UserQuery user = new UserQuery(1, 1234, new int[]{});
        Mockito.when(linker.getUserById(1)).thenReturn(user);

        long burnTime = 18082011;

        DeadlineQuery dl1 = new DeadlineQuery(1, "first", burnTime,
                0, 1, new int[]{1}, false, 0);

        DeadlineQuery dl2 = new DeadlineQuery(2, "second", burnTime,
                0, 1, new int[]{1}, false, 0);

        DeadlineQuery dl3 = new DeadlineQuery(3, "third", burnTime,
                0, 1, new int[]{1}, false, 0);

        DeadlineQuery dl4 = new DeadlineQuery(4, "chotyri", burnTime,
                0, 1, new int[]{1}, false, 0);


        GroupQuery g1 = new GroupQuery(1, "G1", 1, "token1");
        GroupQuery g2 = new GroupQuery(2, "G2", 1, "token2");
        GroupQuery g3 = new GroupQuery(3, "G3", 1, "token3");

        GroupQuery[] groupsForUser = {g1, g2, g3};
        DeadlineQuery[] dlForUser = {dl1, dl2, dl3, dl4};

        Mockito.when(linker.getGroupsDeadlines(new int[]{1})).thenReturn(dlForUser);
        Mockito.when(linker.getGroupsDeadlines(new int[]{2})).thenReturn(new DeadlineQuery[]{});
        Mockito.when(linker.getOwnedGroups(1)).thenReturn(groupsForUser);
        Mockito.when(linker.getGroups(new int[]{1})).thenReturn(new GroupQuery[]{g1});
        Mockito.when(linker.getGroups(new int[]{2})).thenReturn(new GroupQuery[]{g2});
    }

    @ParameterizedTest(name = "Empty set")
    @MethodSource("emptyArgs")
    @DisplayName("Remove without arguments")
    void withoutTest(final int inStateId, final int outStateId, final String answerText)
            throws UserNotFound, CommandException {
        String[] dlNames = new String[] {"first", "second", "third", "chotyri"};

        State state = new State(inStateId, new String[]{}, list);
        if (inStateId == 2) {
            state.setMemory(new String[]{"1"});
        }
        State resultState = list.execute(1, new String[]{}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);
            Mockito.verify(responseQueue, Mockito.times(2)).add(answerCaptor.capture());
        } else {
            Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());
        }
        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        if (inStateId == 2) {
            assertNull(resultState);
            for (String i : dlNames) {
                assertTrue(answer.getString().contains(i));
            }
        } else {
            assertEquals(answerText, answer.getString());
            assertEquals(outStateId, resultState.getStateId());
            assertEquals(state.getOwner(), resultState.getOwner());
        }
    }

    static Stream<Arguments> emptyArgs() {
        return Stream.of(
                Arguments.of(0, 1, "Please enter a valid group id"),
                Arguments.of(1, 1, "Please enter a valid group id"),
                Arguments.of(2, -1, "")
        );
    }

    @ParameterizedTest(name = "Args set")
    @MethodSource("someArgs")
    @DisplayName("Remove with arguments")
    void withArgsTest(final int inStateId, final String[] memory)
            throws UserNotFound, CommandException {
        String[] dlNames = new String[] {"first", "second", "third", "chotyri"};

        State state = new State(inStateId, memory, list);

        State resultState = list.execute(1, new String[]{"1"}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);
            Mockito.verify(responseQueue, Mockito.times(2)).add(answerCaptor.capture());
        } else {
            Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());
        }
        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        for (String i : dlNames) {
            assertTrue(answer.getString().contains(i));
        }

        assertNull(resultState);
    }

    static Stream<Arguments> someArgs() {
        return Stream.of(
                Arguments.of(0, new String[]{}),
                Arguments.of(1, new String[]{"1", "2", "3"}),
                Arguments.of(2, new String[]{"1"})
        );
    }

    @Test
    @DisplayName("Empty group Test")
    void emptyGroupTest() throws UserNotFound, CommandException {
        GroupQuery[] queryList = new GroupQuery[]{};
        Mockito.when(linker.getOwnedGroups(1)).thenReturn(queryList);

        State resultState = list.execute(1, new String[]{"1"}, null,
                responseQueue);
        Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();

        assertEquals(1, answer.getNumber());
        assertEquals("You have no groups", answer.getString());
        assertNull(resultState);
    }

    @Test
    @DisplayName("Empty Task Test")
    void emptyTaskTest() throws UserNotFound, CommandException {


        State resultState = list.execute(1, new String[]{"2"}, null,
                responseQueue);
        Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(2)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();

        assertEquals(1, answer.getNumber());
        assertEquals("You have no tasks", answer.getString());
        assertNull(resultState);
    }

    @ParameterizedTest(name = "Illegal Args set")
    @MethodSource("someIllArgs")
    @DisplayName("Remove with arguments")
    void withIllegalArgsTest(final String arg, final boolean invalid)
            throws UserNotFound, CommandException {

        State state = new State(1, new String[]{"1", "2", "3"}, list);

        State resultState = list.execute(1, new String[]{arg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);

        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());
        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(invalid, answer.getString().equals("Please enter a valid group id"));
        assertEquals(invalid, resultState != null);
    }

    static Stream<Arguments> someIllArgs() {
        return Stream.of(
                Arguments.of("1", false),
                Arguments.of("0", true),
                Arguments.of("7", true)
        );
    }
}
