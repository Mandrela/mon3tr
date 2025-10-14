package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public abstract class InfoCommand implements Command {
    protected String info = "";

    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), info);
        telegramClient.execute(sendMessage);
    }

    /**
     * @param newInfo new info property
    */
    public void setInfo(final String newInfo) {
        info = newInfo;
    }
}
