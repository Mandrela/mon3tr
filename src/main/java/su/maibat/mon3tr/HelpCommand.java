package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class HelpCommand implements Command {
    private Command[] commands = {};

    public final String getName() {
        return "help";
    }

    public final String getHelp() {
        return "HELP ME";
    }

    public final void setCommandsList(final Command[] commandsArgument) {
        commands = commandsArgument;
    }

    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        String answer = "Available commands:\n\n";
        for (int i = 0; i < commands.length; i++) {
            answer += commands[i].getName() + "\n"; // TODO: bytestring optimisation
        }
        answer += "\nType help <command> to see information about specific command";

        SendMessage sendMessage = new SendMessage(chatId.toString(), answer);
        telegramClient.execute(sendMessage);
    }

    public final void executeWithArgs(final Long chatId, final TelegramClient telegramClient,
            final String[] arguments) throws TelegramApiException {
        String answer = "Command not found";

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getName().equals(arguments[0])) {
                answer = commands[i].getName() + "\t---\t" + commands[i].getHelp();
                break;
            }
        }

        SendMessage sendMessage = new SendMessage(chatId.toString(), answer);
        telegramClient.execute(sendMessage);
    }
}
