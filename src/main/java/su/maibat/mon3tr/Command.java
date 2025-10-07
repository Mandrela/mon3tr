package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.generics.TelegramClient;

abstract class Command
{
    public final String name = "";
    public final String helpInfo = "";
    abstract void execute(Long chatId, TelegramClient telegramClient);
}
