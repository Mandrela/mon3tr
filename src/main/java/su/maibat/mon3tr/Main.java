package su.maibat.mon3tr;

import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedHashMap;

import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import su.maibat.mon3tr.commands.AboutCommand;
import su.maibat.mon3tr.commands.AuthorsCommand;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.DeadlineAddCommand;
import su.maibat.mon3tr.commands.DeadlineRemoveCommand;
import su.maibat.mon3tr.commands.HelpCommand;
import su.maibat.mon3tr.commands.MyDeadlinesCommand;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.LinkerException;


public final class Main {
    public static final String DEBUG = "\u001b[1;97m[\u001b[0;90mDEBG\u001b[1;97m]\u001b[0m ";
    public static final String INFO = "\u001b[1;97m[INFO]\u001b[0m ";
    public static final String WARNING = "\u001b[1;97m[\u001b[1;33mWARN\u001b[1;97m]\u001b[0m ";
    public static final String ERROR = "\u001b[1;97m[\u001b[0;31mERRO\u001b[1;97m]\u001b[0m ";
    public static final String CRITICAL = "\u001b[1;97m[\u001b[1;91mCRIT\u001b[1;97m]\u001b[0m ";

    private Main() { }

    /**
     @param args unused
     */
    public static void main(final String[] args) {
        String token = System.getenv("MON3TR_TOKEN");
        if (token == null) {
            System.err.println(CRITICAL + "Environmental variable MON3TR_TOKEN is not set.");
            System.exit(1);
        }

        String dbName = System.getenv("DB_NAME");
        if (dbName == null) {
            dbName = "mon3tr-database.db";
        }


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

        TelegramBotsLongPollingApplication botsApplication =
            new TelegramBotsLongPollingApplication();


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

        Command[] commands = {help, new AboutCommand(), authors, deadlineAddCommand,
            deadlineGetCommand, deadlineRemoveCommand}; // Commands

        LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>();
        for (Command command : commands) {
            commandMap.put(command.getName(), command);
        }
        help.setCommands(commandMap);


        Bot bot = new Bot(token, commandMap, help);
        try {
            BotSession botSession = botsApplication.registerBot(token, bot);
            synchronized (botSession) {
                botSession.wait();
            }
        } catch (Exception e) {
            System.out.println(CRITICAL + e.getMessage());
        } finally {
            try {
                dataBase.close();
                botsApplication.close();
            } catch (Exception e) {
                System.out.println(CRITICAL + "Couldn't release resources properly:\n"
                + e.getMessage());
            }
            System.out.println(INFO + "Terminated, bye!");
        }
    }
}
