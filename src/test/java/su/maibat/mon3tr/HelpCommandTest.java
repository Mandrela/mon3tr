package su.maibat.mon3tr;

import su.maibat.mon3tr.commands.*;

import org.mockito.Mockito;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.stream.Stream;


class HelpCommandTest {
    private HelpCommand command;
    private LinkedHashMap<String, Command> commandMap = new LinkedHashMap<String, Command>();
    private TelegramClient telegramClient = Mockito.mock(TelegramClient.class);

    @BeforeEach
    void setUp() {
        command = new HelpCommand();
        Command[] commands = {command, new AboutCommand(), new AuthorsCommand()};

        for (int i = 0; i < commands.length; i++) {
            commandMap.put(commands[i].getName(), commands[i]);
        }
        command.setCommands(commandMap);

    }

    @Test
    @DisplayName("Shows all commands")
    void AllCommandTest() {
        //assertDoesNotThrow(() -> command.execute(123l, telegramClient), "Should not throw");

        //Mockito.verify(telegramClient).execute(
         //               Mockito.argThat(arg -> arg instanceof SendMessage)); // yobani rot vashego telegrama

//        SendMessage result = (SendMessage) telegramClient.getLastMethod();

//        assertEquals("123", result.getChatId(), "ChatId should be equal");

//        for (String commandName : commandMap.keySet()) {
//            assertTrue(result.getText().contains(commandName),
//                "Text should contain all methods in correct order");
//        }
    }
/*
    @Test
    @DisplayName("Empty command set")
    void EmptyCommandTest() {
        command.setCommands(new LinkedHashMap<String, Command>());

        assertDoesNotThrow(() -> command.execute(123l, telegramClient), "Should not throw");
    }

    @Test
    @DisplayName("Execute with args withour args")
    void YesNoArgs() {
        assertDoesNotThrow(() -> command.executeWithArgs(123l, telegramClient, new String[]{}),
            "Should not throw");

        SendMessage result = (SendMessage) telegramClient.getLastMethod();
        assertEquals("123", result.getChatId(), "ChatId should be equal");
        assertEquals("", result.getText(), "Text should be empty");

    }

    @ParameterizedTest(name = "Test set {1}")
    @MethodSource("helpArgs")
    @DisplayName("Execution with args")
    void ExecWithArgsTest(String[] args, Boolean[] isFound) {
        assertEquals(args.length, isFound.length, "MALFORMED TEST DATA");

        assertDoesNotThrow(() -> command.executeWithArgs(123l, telegramClient, args),
            "Should not throw");

        SendMessage result = (SendMessage) telegramClient.getLastMethod();
        assertEquals("123", result.getChatId(), "ChatId should be equal");

        String[] lines = result.getText().split("\n");
        System.out.println(args.length * 10 + lines.length);
        assertEquals(args.length, lines.length, "Amount of answer lines should match amount of args");

        for (int i = 0; i < lines.length; i++) {
            assertEquals(!isFound[i], lines[i].toLowerCase().contains("not found"),
                "'not found' in '" + args[i] + "' arg line");
        }
    }*/


    static Stream<Arguments> helpArgs() {
        return Stream.of(
            Arguments.of(new String[]{"authors", "about"}, new Boolean[]{true, true}),
            Arguments.of(new String[]{"LOL", "kek", "sabout"}, new Boolean[]{false, false, false}),
            Arguments.of(new String[]{"kek", "", "help", "about"}, new Boolean[]{false, false, true, true})
        );
    }
}
