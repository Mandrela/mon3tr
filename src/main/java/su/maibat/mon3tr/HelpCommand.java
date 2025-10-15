package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.LinkedHashMap;


public class HelpCommand implements Command {
    private static final char PREFIX = '/';
    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();

    public final String getName() {
        return "help";
    }

    public final String getHelp() {
        return "HELP ME";
    }

    /**
     * @param commandsArgument Map where key is a name of the command and value is Command
     * instance.
    */
    public final void setCommands(final LinkedHashMap<String, Command> commandsArgument) {
        commands = commandsArgument;
    }


    /**
     * Supposebly the default function for non valid Bot command. See Bot.java.
     * @param chatId
     * @param telegramClient
    */
    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        String answer = "Available commands:\n\n";
        for (String i : commands.keySet()) {
            answer += PREFIX + i + "\n";
        }
        answer += "\nType help <command> to see information about specific command";

        SendMessage sendMessage = new SendMessage(chatId.toString(), answer.toString());
        telegramClient.execute(sendMessage);
    }


    /**
     * @param chatId
     * @param telegramClient
     * @param arguments List of one-worded commands
    */
    public final void executeWithArgs(final Long chatId, final TelegramClient telegramClient,
            final String[] arguments) throws TelegramApiException {
        String notFoundStr = "Command not found";
        String answer = "";

        for (int i = 0; i < arguments.length; i++) {
            if (commands.containsKey(arguments[i])) {
                answer += arguments[i] + "\t---\t" + commands.get(arguments[i]).getHelp() + "\n";
            } else {
                answer += arguments[i] + "\t---\t" + notFoundStr + "\n";
            }
        }

        SendMessage sendMessage = new SendMessage(chatId.toString(), answer.strip());
        telegramClient.execute(sendMessage);
    }
}
