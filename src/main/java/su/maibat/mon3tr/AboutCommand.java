package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


/**
 * Shows information about bot
*/
public class AboutCommand implements Command {
    private String informationAbout = "This bot will keep an eye on your deadlines and remind "
            + "you of those that are coming, but it doesn't know how to do that yet, but it will"
            + "try its best.";


    public final String getName() {
        return "about";
    }

    public final String getHelp() {
        return "Shows information about bot";
    }

    /**
     * Just send message
    */
    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), informationAbout);
        telegramClient.execute(sendMessage);
    }
}
