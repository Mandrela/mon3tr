package su.maibat.mon3tr;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import su.maibat.mon3tr.chat.TelegramChat;
import su.maibat.mon3tr.commands.Command;


public final class Bot implements LongPollingSingleThreadUpdateConsumer {
    public static final String NAME = "mon3tr";
    private static final char PREFIX = '/';

    private final TelegramClient telegramClient;
    private final LinkedHashMap<String, Command> commands;
    private final Command defaultCommand;

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

                TelegramChat telegramChat = new TelegramChat(
                    update.getMessage().getChatId(), telegramClient);
                telegramChat.addMessages(Arrays.copyOfRange(message, 1, message.length));
                telegramChat.froze();

                // multithreading I want here
                commands.getOrDefault(commandName, defaultCommand).execute(telegramChat);
            } // else {
                // argument passing to known chats
            // }
        }
    }
}
