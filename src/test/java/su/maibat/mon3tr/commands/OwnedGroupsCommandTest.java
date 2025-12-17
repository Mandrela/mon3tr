package su.maibat.mon3tr.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.commands.group.ListGroups;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnedGroupsCommandTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final ListGroups owned = new ListGroups(linker);
    @SuppressWarnings("unused")
    private UserQuery user;
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @BeforeEach
    final void setUp() throws UserNotFound {
        long chatId = 1234;
        int[] groups = {1, 2, 3};
        user = new UserQuery(1, chatId, groups);

    }

    @Test
    void showGroups() throws CommandException, UserNotFound {
        GroupQuery group1 = new GroupQuery(1, "Abra", 1, "token");
        GroupQuery group2 = new GroupQuery(2, "Cadabra", 1, "tokenA");
        GroupQuery group3 = new GroupQuery(3, "ActOrDie", 1, "Tragodia");
        GroupQuery[] groupList = {group1, group2, group3};
        Mockito.when(linker.getOwnedGroups(1)).thenReturn(groupList);

        owned.execute(1, new String[]{}, null, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        for (int i = 0; i < 3; i++) {
            assertTrue(answer.getString().contains(groupList[i].getName()),
                    "Text contains all deadlines");
        }
    }

}
