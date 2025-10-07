package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Authors extends Command
{
    public final String name = "authors";
    public final String helpInfo = "Something about command authors";
    private String taleOfAuthors = "The great Mandrela, beloved member of human race, father to all sons";
    void execute(Long chatId, TelegramClient telegramClient)

    {
        SendMessage sendMessage = new SendMessage(chatId.toString(), taleOfAuthors);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
