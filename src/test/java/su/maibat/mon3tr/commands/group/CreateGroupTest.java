package su.maibat.mon3tr.commands.group;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.SQLiteLinker;

import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CreateGroupTest {
    private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    private final BlockingQueue<NumberedString> responseQueue =
            (BlockingQueue<NumberedString>) Mockito.mock(BlockingQueue.class);
    private final CreateGroup create = new CreateGroup(linker);

    @ParameterizedTest(name = "Name set")
    @MethodSource("nameArgs")
    @DisplayName("Create lonely test")
    void nameArgTest(final String nameArg, final boolean valid)
            throws CommandException {

        State state = new State(0, new String[]{}, create);

        State resultState = create.execute(1, new String[]{nameArg}, state, responseQueue);

        ArgumentCaptor<NumberedString> answerCaptor = ArgumentCaptor.forClass(NumberedString.class);
        Mockito.verify(responseQueue, Mockito.times(1)).add(answerCaptor.capture());

        NumberedString answer = answerCaptor.getValue();
        assertEquals(1, answer.getNumber());
        assertEquals(answer.getString().equals("Group created"), valid);
        assertEquals(answer.getString().equals("Enter group name"), !valid);
        assertEquals(resultState == null, valid);

    }

    static Stream<Arguments> nameArgs() {
        return Stream.of(
                Arguments.of("Paradigmatic", true),
                Arguments.of("27.12.25", true),
                Arguments.of("", false),
                Arguments.of("sikhhuyfiytdffkrdykcitydtdtyktgdktdtdaafshssrgaesfes", false)

        );
    }
}
