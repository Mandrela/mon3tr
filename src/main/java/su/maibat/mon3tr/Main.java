package su.maibat.mon3tr;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public final class Main {
	private Main() { }

	/**
		@param args unused
	*/
	public static void main(final String[] args) {
        String token = System.getenv("MON3TR_TOKEN");
        System.out.println(token);
        /*

    	try {
		    TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
	    	botsApplication.registerBot(token, new Bot(token));
    	} catch (Exception e) {
		    e.printStackTrace();
	    	System.out.println(e.getMessage());
    		System.out.println(e.getCause());
	    }*/
    }
}
