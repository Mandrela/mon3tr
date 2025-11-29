package su.maibat.mon3tr;

import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedHashMap;
// import java.util.concurrent.ArrayBlockingQueue;
// import java.util.concurrent.BlockingQueue;

// import org.telegram.telegrambots.longpolling.BotSession;
// import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import su.maibat.mon3tr.commands.AboutCommand;
import su.maibat.mon3tr.commands.AuthorsCommand;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.DeadlineAddCommand;
import su.maibat.mon3tr.commands.DeadlineRemoveCommand;
import su.maibat.mon3tr.commands.HelpCommand;
import su.maibat.mon3tr.commands.MyDeadlinesCommand;
import su.maibat.mon3tr.commands.UpdateOffsetCommand;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.LinkerException;
// import su.maibat.mon3tr.telegramwrap.Gate;


public final class Main {
    public static final String DEBUG = "\u001b[1;97m[\u001b[0;90mDEBG\u001b[1;97m]\u001b[0m ";
    public static final String INFO = "\u001b[1;97m[INFO]\u001b[0m ";
    public static final String WARNING = "\u001b[1;97m[\u001b[1;33mWARN\u001b[1;97m]\u001b[0m ";
    public static final String ERROR = "\u001b[1;97m[\u001b[0;31mERRO\u001b[1;97m]\u001b[0m ";
    public static final String CRITICAL = "\u001b[1;97m[\u001b[1;91mCRIT\u001b[1;97m]\u001b[0m ";
    public static final int MINUTE_TIME_SEC = 60;
    public static final int SEC_TO_MILLIS_FACTOR = 1000;
    public static final int DAY_SEC = 86400;

    private Main() { }

    /**
     @param args unused
     */
    public static void main(final String[] args) {
        //BlockingQueue<Pair<int, String>> queue = new ArrayBlockingQueue<>();

        //new Responder(new ConcurrentHashMap<Int, Long>());
        //new Gate(new ConcurrentHashMap<Int, Long>());

        // Settings
        String token = System.getenv("MON3TR_TOKEN");
        if (token == null) {
            System.err.println(CRITICAL + "Environmental variable MON3TR_TOKEN is not set.");
            System.exit(1);
        }

        String dbName = System.getenv("DB_NAME");
        if (dbName == null) {
            dbName = "mon3tr-database.db";
        }


        // Common resources
        SQLiteLinker dataBase;
        try {
            dataBase = new SQLiteLinker(dbName);
        } catch (FileAlreadyExistsException e) {
            System.out.println(CRITICAL + dbName + " is a directory");
            return;
        } catch (LinkerException e) {
            System.out.println(CRITICAL + "Got error trying to initialize database:\n"
                + e.getMessage());
            return;
        }

        // TelegramBotsLongPollingApplication botsApplication =
        //     new TelegramBotsLongPollingApplication();


        // Commands
        HelpCommand help = new HelpCommand();

        AuthorsCommand authors = new AuthorsCommand();
        String customAuthors = System.getenv("AUTHORS");
        if (customAuthors != null) {
            authors.setInfo(customAuthors);
            System.out.println(INFO + "Using custom authors info.");
        }

        DeadlineAddCommand deadlineAddCommand = new DeadlineAddCommand(dataBase);
        MyDeadlinesCommand deadlineGetCommand = new MyDeadlinesCommand(dataBase);
        DeadlineRemoveCommand deadlineRemoveCommand = new DeadlineRemoveCommand(dataBase);


        UpdateOffsetCommand updateOffsetCommand = new UpdateOffsetCommand(dataBase);

        Command[] commands = {help, new AboutCommand(), authors, deadlineAddCommand,
                deadlineGetCommand, deadlineRemoveCommand, updateOffsetCommand}; // Commands

        LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>();
        for (Command command : commands) {
            commandMap.put(command.getName(), command);
        }
        help.setCommands(commandMap);


        // Workers
        // Bot bot = new Bot(token, commandMap, help);
        // Notifier notifier = new Notifier(dataBase, bot.getTelegramClient());

        // Thread notifierThread = new Thread(getExpireTime);
        // notifierThread.start();

        // try {
        //     BotSession botSession = botsApplication.registerBot(token, bot);
        //     synchronized (botSession) {
        //         botSession.wait();
        //     }
        // } catch (Exception e) {
        //     System.out.println(CRITICAL + e.getMessage());
        // } finally {
        //     System.out.println(INFO + "Started finalization process.");

        //     notifierThread.interrupt();
        //     try {
        //         notifierThread.join(MINUTE_TIME_SEC * SEC_TO_MILLIS_FACTOR);
        //         if (notifierThread.isAlive()) {
        //             throw new InterruptedException("Notifier wasn't dying during whole minute.");
        //         }
        //     } catch (InterruptedException e) {
        //         System.out.println(ERROR + "Couldn't stop notifier module properly:\n"
        //             + e.getMessage());
        //     }

        //     try {
        //         dataBase.close();
        //         botsApplication.close();
        //     } catch (Exception e) {
        //         System.out.println(ERROR + "Couldn't release resources properly:\n"
        //         + e.getMessage());
        //     }

        //     System.out.println(INFO + "Terminated, bye!");
        // }
    }
}
