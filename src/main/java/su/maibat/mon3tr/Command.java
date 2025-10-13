package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public interface Command {
    String getName();
    String getHelp();

    void execute(Long chatId, TelegramClient telegramClient) throws TelegramApiException;

    default void executeWithArgs(Long chatId, TelegramClient telegramClient, String[] arguments)
            throws TelegramApiException {
        execute(chatId, telegramClient);
    }
}
