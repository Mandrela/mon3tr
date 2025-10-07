package su.maibat.mon3tr;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

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

        Command[] commands = {new HelpCommand(), new AboutCommand(), new AuthorsCommand()};
        commands[0].setCommandsList(commands);
        Bot bot = new Bot(token, commands);

        try {
            botsApplication.registerBot(token, bot);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
    }
}
