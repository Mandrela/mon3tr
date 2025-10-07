package su.maibat.mon3tr;

import java.util.Optional;
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

    	try {
		    TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
	    	botsApplication.registerBot(token, new Bot(token));
    	} catch (Exception e) {
		    e.printStackTrace();
	    	System.out.println(e.getMessage());
    		System.out.println(e.getCause());
	    }
    }
}
