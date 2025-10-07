package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class About
{
    public final String name = "about";
    public final String helpInfo = "Something about command about";
    private String informationAbout = "This bot will keep an eye on your deadlines and remind you of those that are coming," +
            " but it doesn't know how to do that yet, but it will try its best.";
    void execute(Long chatId, TelegramClient telegramClient)
    {
        SendMessage sendMessage = new SendMessage(chatId.toString(), informationAbout);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
