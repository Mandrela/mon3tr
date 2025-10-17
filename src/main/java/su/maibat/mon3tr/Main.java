package su.maibat.mon3tr;

import java.util.LinkedHashMap;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import su.maibat.mon3tr.commands.AboutCommand;
import su.maibat.mon3tr.commands.AuthorsCommand;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.HelpCommand;


public final class Main {
    private Main() { }

    /**
     @param args unused
     */
    public static void main(final String[] args) {
        String token = System.getenv("MON3TR_TOKEN");
        if (token == null) {
            System.err.println("Environmental variable MON3TR_TOKEN is not set");
            System.exit(1);
        }

        TelegramBotsLongPollingApplication botsApplication =
                new TelegramBotsLongPollingApplication();

        HelpCommand help = new HelpCommand();
        AuthorsCommand authors = new AuthorsCommand();

        String customAuthors = System.getenv("AUTHORS");
        if (customAuthors != null) {
            authors.setInfo(customAuthors);
        }

        Command[] commands = {help, new AboutCommand(), authors}; // Commands

        LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>();
        for (Command command : commands) {
            commandMap.put(command.getName(), command);
        }
        help.setCommands(commandMap);

        Bot bot = new Bot(token, commandMap, help);
        try {
            botsApplication.registerBot(token, bot);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
    }
}
