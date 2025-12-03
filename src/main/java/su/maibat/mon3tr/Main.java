package su.maibat.mon3tr;

import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedHashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import su.maibat.mon3tr.bot.Bot;
import su.maibat.mon3tr.bot.BotBackend;
import su.maibat.mon3tr.commands.AboutCommand;
import su.maibat.mon3tr.commands.AuthorsCommand;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.DeadlineAddCommand;
import su.maibat.mon3tr.commands.DeadlineRemoveCommand;
import su.maibat.mon3tr.commands.GroupCreateCommand;
import su.maibat.mon3tr.commands.GroupDeleteCommand;
import su.maibat.mon3tr.commands.GroupJoinCommand;
import su.maibat.mon3tr.commands.HelpCommand;
import su.maibat.mon3tr.commands.ListGroupTaskCommand;
import su.maibat.mon3tr.commands.MoveToGroupCommand;
import su.maibat.mon3tr.commands.MyDeadlinesCommand;
import su.maibat.mon3tr.commands.OwnedGroupsCommand;
import su.maibat.mon3tr.commands.RegisterCommand;
import su.maibat.mon3tr.commands.RemoveFromGroupCommand;
import su.maibat.mon3tr.commands.UpdateOffsetCommand;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.LinkerException;
import su.maibat.mon3tr.notifier.Notifier;
import su.maibat.mon3tr.telegramwrap.Gate;
import su.maibat.mon3tr.telegramwrap.Responder;


public final class Main {
    public static final String DEBUG = "\u001b[1;97m[\u001b[0;90mDEBG\u001b[1;97m]\u001b[0m ";
    public static final String INFO = "\u001b[1;97m[INFO]\u001b[0m ";
    public static final String WARNING = "\u001b[1;97m[\u001b[1;33mWARN\u001b[1;97m]\u001b[0m ";
    public static final String ERROR = "\u001b[1;97m[\u001b[0;31mERR\u001b[1;97m]\u001b[0m ";
    public static final String CRITICAL = "\u001b[1;97m[\u001b[1;91mCRIT\u001b[1;97m]\u001b[0m ";

    private static final int DEFAULT_QUEUE_CAPACITY = 8;

    private Main() { }

    /**
     @param args unused
     */
    public static void main(final String[] args) {
        // Settings
        String token = System.getenv("MON3TR_TOKEN");
        if (token == null) {
            System.err.println(CRITICAL + "Environmental variable MON3TR_TOKEN is not set.");
            System.exit(1);
        }

        int queueCapacity = DEFAULT_QUEUE_CAPACITY;
        String rawQueueCapacity = System.getenv("RESP_CAPACITY");
        if (rawQueueCapacity != null) {
            try {
                queueCapacity = Integer.parseInt(rawQueueCapacity);
            } catch (NumberFormatException e) {
                System.err.println(CRITICAL + "Environmental variable RESP_CAPACITY is set to "
                    + "garbage: " + rawQueueCapacity);
            }
        }

        String dbName = System.getenv("DB_NAME");
        if (dbName == null) {
            dbName = "mon3tr-database.db";
        }

        String customAuthors = System.getenv("AUTHORS");


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

        TelegramBotsLongPollingApplication botsApplication =
            new TelegramBotsLongPollingApplication();
        TelegramClient telegramClient = new OkHttpTelegramClient(token);


        // Commands
        WeakHashMap<Integer, Long> uidMap = new WeakHashMap<>();
        RegisterCommand register = new RegisterCommand(dataBase, uidMap);

        HelpCommand help = new HelpCommand();

        AuthorsCommand authors = new AuthorsCommand();
        if (customAuthors != null) {
            authors.setInfo(customAuthors);
            System.out.println(INFO + "Using custom authors info.");
        }

        BlockingQueue<NumberedString> queue = new ArrayBlockingQueue<NumberedString>(queueCapacity);
        Notifier notifier = new Notifier(dataBase, queue);

        DeadlineAddCommand deadlineAddCommand = new DeadlineAddCommand(dataBase, notifier);
        MyDeadlinesCommand deadlineGetCommand = new MyDeadlinesCommand(dataBase);
        DeadlineRemoveCommand deadlineRemoveCommand = new DeadlineRemoveCommand(dataBase);
        UpdateOffsetCommand updateOffsetCommand = new UpdateOffsetCommand(dataBase, notifier);

        GroupCreateCommand groupCreateCommand = new GroupCreateCommand(dataBase);
        OwnedGroupsCommand ownedGroupsCommand = new OwnedGroupsCommand(dataBase);
        GroupDeleteCommand groupDeleteCommand = new GroupDeleteCommand(dataBase);

        GroupJoinCommand groupJoinCommand = new GroupJoinCommand(dataBase);

        MoveToGroupCommand moveToGroupCommand = new MoveToGroupCommand(dataBase);
        ListGroupTaskCommand listGroupTaskCommand = new ListGroupTaskCommand(dataBase);
        RemoveFromGroupCommand removeFromGroupCommand = new RemoveFromGroupCommand(dataBase);


        Command[] commands = {help, register, new AboutCommand(), authors,
                deadlineAddCommand, deadlineGetCommand, deadlineRemoveCommand,
                updateOffsetCommand, groupCreateCommand, ownedGroupsCommand,
                groupDeleteCommand, groupJoinCommand, moveToGroupCommand,
                listGroupTaskCommand, removeFromGroupCommand};

        LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>();
        for (Command command : commands) {
            commandMap.put(command.getName(), command);
        }
        help.setCommands(commandMap);


        // Workers
        BotBackend bot = new Bot(dataBase, help, register, commandMap, queue);

        Responder responder = new Responder(telegramClient, queue, dataBase, uidMap);
        Thread responderThread = new Thread(responder);
        responderThread.start();

        Gate gate = new Gate(bot, dataBase, uidMap);

        Thread notifierThread = new Thread(notifier);
        notifierThread.setDaemon(true);
        notifierThread.start();

        try {
            BotSession botSession = botsApplication.registerBot(token, gate);
            synchronized (botSession) {
                botSession.wait();
            }
        } catch (Exception e) {
            System.out.println(CRITICAL + e.getMessage());
        } finally {
            System.out.println(INFO + "Started finalization process.");

            notifierThread.interrupt();
            responderThread.interrupt();

            try {
                dataBase.close();
                botsApplication.close();
                responderThread.join();
            } catch (Exception e) {
                System.out.println(ERROR + "Couldn't release resources properly:\n"
                + e.getMessage());
            }
            System.out.println(INFO + "Terminated, bye!");
        }
    }
}
