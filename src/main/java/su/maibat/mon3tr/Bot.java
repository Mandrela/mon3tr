package su.maibat.mon3tr;


import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class Bot implements LongPollingSingleThreadUpdateConsumer {

    /*public static final String NAME = "mon3tr";
    public static final String TOKEN = "";


    public String getToken() {
        return TOKEN;
    }
    public String getName() {
        return NAME;
    }*/
    private TelegramClient telegramClient = new OkHttpTelegramClient("");
    @Override
    public void consume(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), update.getMessage().getText());
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
    }

}
