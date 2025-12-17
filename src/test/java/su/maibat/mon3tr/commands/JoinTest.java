package su.maibat.mon3tr.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.group.Join;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JoinTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final Join join = new Join(linker);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @ParameterizedTest(name = "Name set")
    @MethodSource("nameArgs")
    @DisplayName("Create lonely test")
    void nameArgTest(final String nameArg, final String result, final boolean valid)
            throws MalformedQuery, UserNotFound, TokenNotFound {

        GroupQuery group = new GroupQuery(1,  "Test", 1, "1234Test");
        UserQuery user1 = new UserQuery(2, 1234, new int[]{});
        user1.setMembership(new int[]{});
        Mockito.when(linker.getUserById(2)).thenReturn(user1);
        if (nameArg.equals("1234Test")) {
            Mockito.when(linker.tryFindToken(nameArg)).thenReturn(group);
        } else {
            Mockito.when(linker.tryFindToken(nameArg)).thenThrow(new TokenNotFound(nameArg));
        }
        State state = new State(0, new String[]{}, join);
        UserQuery user2 = new UserQuery(2, 1234, new int[]{});
        user2.setMembership(new int[]{1});

        State resultState = join.execute(2, new String[]{nameArg}, state, responseQueue);
        Mockito.verify(linker, Mockito.times(1)).tryFindToken(nameArg);
        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(2, answer.getNumber());
        assertEquals(result, answer.getString());
        assertEquals(valid, resultState == null);


    }

    static Stream<Arguments> nameArgs() {
        return Stream.of(
                Arguments.of("1234Test", "Membership aquired", true),
                Arguments.of("12355555Test", "Token is not valid", false),
                Arguments.of("27.12.25", "Token is not valid", false),
                Arguments.of("", "Token is not valid", false)
        );
    }
}
