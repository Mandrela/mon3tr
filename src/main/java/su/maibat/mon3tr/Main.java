package su.maibat.mon3tr;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.LinkedHashMap;

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

        Command[] commands = {help, new AboutCommand(), new AuthorsCommand()}; // Commands

        LinkedHashMap<String, Command> commandMap = new LinkedHashMap<String, Command>();
        for (int i = 0; i < commands.length; i++) {
            commandMap.put(commands[i].getName(), commands[i]);
        }
        help.setCommandsList(commandMap);

        Bot bot = new Bot(token, commandMap);
        try {
            botsApplication.registerBot(token, bot);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
    }
}
