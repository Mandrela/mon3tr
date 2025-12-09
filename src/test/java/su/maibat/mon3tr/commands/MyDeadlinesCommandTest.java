package su.maibat.mon3tr.commands;

import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;

import static org.junit.jupiter.api.Assertions.*;


public final class MyDeadlinesCommandTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final MyDeadlinesCommand show = new MyDeadlinesCommand(linker);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);

    @Test
    @DisplayName("Shows for user with deadlines")
    void correctShowTest() throws DeadlineNotFound {
        UserQuery user = new UserQuery(0, 1234, new int[]{});

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

        Mockito.when(linker.getDeadlinesForUser(0)).thenReturn(deadlinesForUser);

        assertDoesNotThrow(() -> show.execute(0, new String[]{}, null, responseQueue),
                "Should not throw");

        Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(0);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        NumberedString answer = answerCaptor.getValue();

        for (int i = 0; i < 4; i++) {
            assertTrue(answer.getString().contains(deadlinesForUser[i].getName()),
                    "Text contains all deadlines");
        }
    }

    @Test
    @DisplayName("Shows for user without deadlines")
    void emptyUserTest() throws DeadlineNotFound {
        DeadlineQuery[] deadlinesForUser = new DeadlineQuery[]{};
        Mockito.when(linker.getDeadlinesForUser(0)).thenReturn(deadlinesForUser);

        assertDoesNotThrow(() -> show.execute(0, new String[]{}, null, responseQueue),
                "Should not throw");

        Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(0);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        NumberedString answer = answerCaptor.getValue();

        assertEquals("You have not any deadlines", answer.getString());
    }
}
