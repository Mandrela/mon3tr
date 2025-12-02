package su.maibat.mon3tr.bot;

public interface BotBackend {
    char getCommandPrefix();
    void process(int userId, String command) throws BotException;
}
