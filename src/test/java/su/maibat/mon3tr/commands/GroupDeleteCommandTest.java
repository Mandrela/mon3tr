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
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class GroupDeleteCommandTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final GroupDeleteCommand remove = new GroupDeleteCommand(linker);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @BeforeEach
    void setUp() throws UserNotFound {

        UserQuery user = new UserQuery(1, 1234, new int[]{});
        Mockito.when(linker.getUserById(1)).thenReturn(user);


        GroupQuery g1 = new GroupQuery("G1", 1);
        GroupQuery g2 = new GroupQuery("G2", 1);
        GroupQuery g3 = new GroupQuery("G3", 1);

        GroupQuery[] groupsForUser = {g1, g2, g3};

        Mockito.when(linker.getOwnedGroups(1)).thenReturn(groupsForUser);
    }

    @ParameterizedTest(name = "Empty set")
    @MethodSource("emptyArgs")
    @DisplayName("Remove without arguments")
    void withoutTest(final int inStateId, final int outStateId, final String answerText)
            throws CommandException, UserNotFound {

        State state = new State(inStateId, new String[]{}, remove);
        State resultState = remove.execute(1, new String[]{}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);
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
                Arguments.of(0, 1, "Please enter a valid group id (number)"),
                Arguments.of(1, 1, "Please enter a valid group id (number)")
        );
    }

    @ParameterizedTest(name = "Args set")
    @MethodSource("someArgs")
    @DisplayName("Remove with arguments")
    void withArgsTest(final int inStateId, final String answerText)
            throws CommandException, UserNotFound {

        State state = new State(inStateId, new String[]{}, remove);
        if (inStateId == 1) {
            state.setMemory(new String[]{"1", "2", "3"});
        }
        State resultState = remove.execute(1, new String[]{"1"}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        if (inStateId == 0) {
            Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);
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
                Arguments.of(0, "You have remove group"),
                Arguments.of(1, "You have remove group")
        );
    }

    @Test
    @DisplayName("Empty group Test")
    void emptyGroupTest() throws CommandException, DeadlineNotFound, UserNotFound {
        GroupQuery[] queryList = new GroupQuery[]{};
        Mockito.when(linker.getOwnedGroups(1)).thenReturn(queryList);

        State resultState = remove.execute(1, new String[]{"1"}, null,
                responseQueue);
        Mockito.verify(linker, Mockito.times(1)).getOwnedGroups(1);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();

        assertEquals(1, answer.getNumber());
        assertEquals("You have not any groups", answer.getString());
        assertNull(resultState);
    }
}
