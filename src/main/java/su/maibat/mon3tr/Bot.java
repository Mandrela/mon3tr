package su.maibat.mon3tr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import su.maibat.mon3tr.chat.MessageSink;
import su.maibat.mon3tr.chat.TelegramChat;
import su.maibat.mon3tr.commands.Command;


public final class Bot implements LongPollingSingleThreadUpdateConsumer {
    public static final String NAME = "mon3tr";
    private static final char PREFIX = '/';

    private final HashMap<Long, MessageSink> chatMap = new HashMap<>();
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


    /**
     * Parses string to array of arguments, where the first argument is a command itself without.
     * a prefix
     * @param textString Input String
     * @return Array of arguments where the first one is a prefix-less command
     */
    private String[] parseCommand(final String textString) {
        if (textString.charAt(0) == PREFIX) {
            String[] result = textString.split(" ");
            result[0] = result[0].substring(1);
            return result;
        }
        return null;
    }


    @Override
    public void consume(final Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            Long chatId = message.getChatId();
            String[] arguments = parseCommand(message.getText());

            if (arguments != null) {
                chatMap.computeIfPresent(chatId,
                    (key, value) -> {
                        value.interrupt(); return null;
                    });

                TelegramChat telegramChat = new TelegramChat(chatId, telegramClient);
                telegramChat.addMessages(Arrays.copyOfRange(arguments, 1, arguments.length));
                telegramChat.freeze();
                chatMap.put(chatId, telegramChat);

                Command commandToExecute = commands.getOrDefault(arguments[0].toLowerCase(),
                    defaultCommand);
                new Thread(() -> {
                    commandToExecute.execute(telegramChat);
                    chatMap.computeIfPresent(chatId, (key, value) -> {
                            value.interrupt(); return null;
                    });
                }).start();

            } else if (chatMap.containsKey(chatId)) {
                chatMap.get(chatId).addMessage(message.getText());
            } else {
                defaultCommand.execute(new TelegramChat(chatId, telegramClient));
            }
        }
    }
}
