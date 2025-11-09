package su.maibat.mon3tr.commands;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import su.maibat.mon3tr.chat.Chat;


class HelpCommandTest {
    private final HelpCommand helpCommand = new HelpCommand();
    private final LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>();
    private final Chat chat = Mockito.mock(Chat.class);

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        String[] data = {};
        Mockito.when(chat.getAllMessages()).thenReturn(data);

        Command[] commands = {helpCommand, new AboutCommand(), new AuthorsCommand()};

        for (Command command1 : commands) {
            commandMap.put(command1.getName(), command1);
        }
        helpCommand.setCommands(commandMap);
    }

    @Test
    @DisplayName("Shows all commands")
    void allCommandTest() {
        assertDoesNotThrow(() -> helpCommand.execute(chat), "Should not throw");

        // Should read arguments only once
        Mockito.verify(chat, Mockito.times(1)).getAllMessages();

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();

        for (String commandName : commandMap.keySet()) {
            assertTrue(answer.contains(commandName),
                "Text should contain all methods in correct order");
        }
    }

    @Test // TODO: readme, UX date, tests, Fixture
    @DisplayName("Empty command set")
    void emptyCommandTest() {
        helpCommand.setCommands(new LinkedHashMap<>());

        assertDoesNotThrow(() -> helpCommand.execute(chat), "Should not throw");
    }

    @ParameterizedTest(name = "Test set {1}")
    @MethodSource("helpArgs")
    @DisplayName("Execution with args")
    void execWithArgsTest(final String[] args, final Boolean[] isFound) {
        assertEquals(args.length, isFound.length, "MALFORMED TEST DATA");
        Mockito.when(chat.getAllMessages()).thenReturn(args);

        assertDoesNotThrow(() -> helpCommand.execute(chat),
            "Should not throw");

        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

        assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
        String answer = answerCaptor.getValue();


        String[] lines = answer.split("\n");
        // System.out.println(args.length * 10 + lines.length);
        assertEquals(args.length, lines.length,
            "Amount of answer lines should match amount of args");

        for (int i = 0; i < lines.length; i++) {
            assertEquals(!isFound[i], lines[i].toLowerCase().contains("not found"),
                "'not found' in '" + args[i] + "' arg line");
        }
    }


    @SuppressWarnings("unused")
    static Stream<Arguments> helpArgs() {
        return Stream.of(
            Arguments.of(new String[]{"authors", "about"}, new Boolean[]{true, true}),
            Arguments.of(new String[]{"LOL", "kek", "sabout"}, new Boolean[]{false, false, false}),
            Arguments.of(new String[]{"kek", "", "help", "about"},
                        new Boolean[]{false, false, true, true})
        );
    }
}
