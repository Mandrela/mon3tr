package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class HelpCommand implements Command {
    public final String getName() {
        return "";
    }

    public final String getHelp() {
        return "";
    }

    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        String answer = "Available commands:";
        SendMessage sendMessage = new SendMessage(chatId.toString(), answer);
        telegramClient.execute(sendMessage);
    }

    public final void executeWithArguments(final Long chatId, final TelegramClient telegramClient,
            final String[] arguments) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), arguments[0]);
        telegramClient.execute(sendMessage);
    }
}
