package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.generics.TelegramClient;

abstract class Command
{
    public final String name = "Name of command";
    final String helpInfo = "Something about command";
    abstract void execute(Long chatId, TelegramClient telegramClient);
}
