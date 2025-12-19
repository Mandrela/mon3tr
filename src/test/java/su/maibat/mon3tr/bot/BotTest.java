package su.maibat.mon3tr.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.StatelessCommand;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;

public final class BotTest {
    private Bot bot;
    private DataBaseLinker db;
    private StatelessCommand defaultCommand;
    private StatelessCommand registerCommand;
    private BlockingQueue<NumberedString> queue;
    private Map<String, Command> commands;
    private Command command1;
    private Command command2;
    private char prefix;


    @BeforeEach
    void mockInit() {
        db = Mockito.mock(DataBaseLinker.class);
        defaultCommand = Mockito.mock(StatelessCommand.class);
        registerCommand = Mockito.mock(StatelessCommand.class);
        queue = new ArrayBlockingQueue<>(8);
        commands = new HashMap<>();

        command1 = Mockito.mock(Command.class);
        command2 = Mockito.mock(Command.class);
        commands.put("command1", command1);
        commands.put("command2", command2);

        bot = new Bot(db, defaultCommand, registerCommand, commands, queue);
        prefix = bot.getCommandPrefix();


        Mockito.when(db.checkUserExists(1)).thenReturn(false);
        Mockito.when(db.checkUserExists(2)).thenReturn(true);
    }

    @Test
    void initializationTest() {
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(null, null, null, null, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(db, defaultCommand, registerCommand, commands, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(db, defaultCommand, registerCommand, null, queue));
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(db, defaultCommand, null, commands, queue));
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(db, null, registerCommand, commands, queue));
        assertThrows(IllegalArgumentException.class,
            () -> new Bot(null, defaultCommand, registerCommand, commands, queue));
    }

    @Test
    void unknownUsersTest() {
        bot.process(-1, prefix + "command1");
        bot.process(1, prefix + "command2");

        // Should not ask database if id is negative, optimisation!
        Mockito.verify(db, Mockito.times(1)).checkUserExists(Mockito.anyInt());
        // // Both users are unregistered, so
        Mockito.verify(registerCommand, Mockito.times(2)).executeWithoutState(
            Mockito.anyInt(),
            Mockito.any(),
            Mockito.any()
        );
    }

    @Test
    void unknownCommandTest() throws CommandException {
        bot.process(2, prefix + "command3");

        Mockito.verify(defaultCommand, Mockito.times(1)).execute(
            Mockito.eq(2), Mockito.any(), Mockito.any(), Mockito.any()
        );
    }


    @Test
    void stateSaveTest() throws CommandException {
        Mockito.when(command1.execute(
            Mockito.eq(2), Mockito.any(), Mockito.isNull(), Mockito.any())
        ).thenReturn(new State(3, new String[]{}, command1));


        bot.process(2, prefix + "command1");

        bot.process(1, prefix + "command2");

        bot.process(2, "data");

        Mockito.verify(defaultCommand, Mockito.never()).executeWithoutState(
            Mockito.anyInt(), Mockito.any(), Mockito.any()
        );

        ArgumentCaptor<State> captor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(command1, Mockito.times(2)).execute(
            Mockito.eq(2), Mockito.any(), captor.capture(), Mockito.any()
        );

        assertEquals(null, captor.getAllValues().get(0));
        State savedState = captor.getAllValues().get(1);
        assertEquals(3, savedState.getStateId());
        assertEquals(0, savedState.getMemory().length);
        assertEquals(command1, savedState.getOwner());
    }
}
