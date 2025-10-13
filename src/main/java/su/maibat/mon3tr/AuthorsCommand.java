package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class AuthorsCommand implements Command {
    private String taleOfAuthors = "The great Mandrela, beloved member of human race, father to "
            + "all sons";

    public final String getName() {
        return "authors";
    }

    public final String getHelp() {
        return "Some help with that";
    }

    /**
     * Just send message.
    */
    public final void execute(final Long chatId, final TelegramClient telegramClient)
            throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), taleOfAuthors);
        telegramClient.execute(sendMessage);
    }

    /**
     * Set new tale, I intended only one use.
    */
    public setTale(final String newTale) {
        taleOfAuthors = newTale;
    }
}
