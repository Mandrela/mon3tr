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
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.InfoStub;
import su.maibat.mon3tr.commands.general.About;
import su.maibat.mon3tr.commands.general.Authors;
import su.maibat.mon3tr.commands.general.Help;
import su.maibat.mon3tr.commands.general.Register;
import su.maibat.mon3tr.commands.group.CreateGroup;
import su.maibat.mon3tr.commands.group.DeleteGroup;
import su.maibat.mon3tr.commands.group.Invite;
import su.maibat.mon3tr.commands.group.Join;
import su.maibat.mon3tr.commands.group.Leave;
import su.maibat.mon3tr.commands.group.Membership;
import su.maibat.mon3tr.commands.group.ListGroups;
import su.maibat.mon3tr.commands.task.AddTask;
import su.maibat.mon3tr.commands.task.RemoveTask;
import su.maibat.mon3tr.commands.task.ListAssignedTasks;
import su.maibat.mon3tr.commands.task.ListMembershipTasks;
import su.maibat.mon3tr.commands.task.Assign;
import su.maibat.mon3tr.commands.task.ListPersonalTasks;
import su.maibat.mon3tr.commands.task.Resign;
import su.maibat.mon3tr.commands.task.Postpone;
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
        Register register = new Register(dataBase, uidMap);

        Help help = new Help();

        Authors authors = new Authors();
        if (customAuthors != null) {
            authors.setInfo(customAuthors);
            System.out.println(INFO + "Using custom authors info.");
        }

        BlockingQueue<NumberedString> queue = new ArrayBlockingQueue<NumberedString>(queueCapacity);
        Notifier notifier = new Notifier(dataBase, queue);

        AddTask taskAdd = new AddTask(dataBase, notifier);
        ListPersonalTasks taskList = new ListPersonalTasks(dataBase);
        RemoveTask taskRemove = new RemoveTask(dataBase);
        Postpone postpone = new Postpone(dataBase, notifier);

        CreateGroup groupCreate = new CreateGroup(dataBase);
        ListGroups groupList = new ListGroups(dataBase);
        DeleteGroup groupDelete = new DeleteGroup(dataBase);

        Join join = new Join(dataBase);
        Invite invite = new Invite(dataBase);
        Membership membership = new Membership(dataBase);
        Leave leave = new Leave(dataBase);

        Assign assign = new Assign(dataBase);
        ListAssignedTasks taskListAssigned = new ListAssignedTasks(dataBase);
        Resign resign = new Resign(dataBase);
        ListMembershipTasks taskListMembershiped = new ListMembershipTasks(dataBase);


        Command[] commands = {
            new InfoStub("\nGeneral:"),
            help,
            register,
            new About(),
            authors,
            new InfoStub("\nTasks:"),
            taskAdd,
            taskList,
            taskRemove,
            postpone,
            new InfoStub(""),
            assign,
            taskListAssigned,
            resign,
            new InfoStub("\nGroups:"),
            groupCreate,
            groupList,
            groupDelete,
            invite,
            new InfoStub("\nMembership:"),
            join,
            membership,
            leave,
            taskListMembershiped,
        };

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
