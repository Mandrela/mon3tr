package su.maibat.mon3tr;

import su.maibat.mon3tr.commands.*;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.LinkedHashMap;


public final class Bot implements LongPollingSingleThreadUpdateConsumer {
    public static final String NAME = "mon3tr";
    private static final char PREFIX = '/';

    private TelegramClient telegramClient;
    private LinkedHashMap<String, Command> commands;
    private Command defaultCommand;

    /**
     * @param token Telegram token -- KEEP SAFE, DO NOT SHARE IT
     * @param commandsArgument Map were key is a name of a command in bot's interface
     * and value is an instance of Command implementing class
     * @param defaultCommandArgument The default command which will be executed if incorrect
     * command supplied
    */
    public Bot(final String token, final LinkedHashMap<String, Command> commandsArgument,
                final Command defaultCommandArgument) {
        telegramClient = new OkHttpTelegramClient(token);
        commands = commandsArgument;
        defaultCommand = defaultCommandArgument;
    }

    @Override
    public void consume(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String[] message = update.getMessage().getText().split(" ");

            if (message[0].charAt(0) == PREFIX) {
                String commandName = message[0].substring(1).toLowerCase();

                boolean notDefaultCommand = true;
                Command command = null;

                if (commands.containsKey(commandName)) {
                    command = commands.get(commandName);
                } else {
                    command = defaultCommand;
                    notDefaultCommand = false;
                }

                try {
                    if (message.length > 1 && notDefaultCommand) {
                        command.executeWithArgs(update.getMessage().getChatId(), telegramClient,
                            Arrays.copyOfRange(message, 1, message.length));
                    } else {
                        command.execute(update.getMessage().getChatId(), telegramClient);
                    }
                } catch (TelegramApiException e) { }
            } // else {
                // argument passing
            // }
        }
    }
}
