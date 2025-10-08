package su.maibat.mon3tr;


import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;


public class Bot implements LongPollingSingleThreadUpdateConsumer {
    public static final String NAME = "mon3tr";
    private static final char PREFIX = '/';
    private TelegramClient telegramClient;
    private Command[] commands;

    public Bot(final String token, final Command[] commandsArgument) {
        telegramClient = new OkHttpTelegramClient(token);
        commands = commandsArgument;
    }

    @Override
    public final void consume(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            /*SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                                        update.getMessage().getText());
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }*/
            String[] message = update.getMessage().getText().split(" ");
            if (message[0].charAt(0) == PREFIX) {
                String commandName = message[0].substring(1).toLowerCase();
                for (int i = 0; i < commands.length; i++) {
                    if (commandName.equals(commands[i].getName())) {
                        try {
                        // TODO:
                            if (message.length == 1) {
                                commands[i].execute(update.getMessage().getChatId(),
                                                        telegramClient);
                            } else {
                                System.out.println(message[1]);
                                commands[i].executeWithArgs(update.getMessage().getChatId(),
                                    telegramClient, Arrays.copyOfRange(message, 1, message.length));
                            }
                        } catch (TelegramApiException e) { // TODO CustomException with help handler
                           throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
