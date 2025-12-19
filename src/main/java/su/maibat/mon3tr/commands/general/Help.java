package su.maibat.mon3tr.commands.general;

import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.InfoStub;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.StatelessCommand;


public final class Help implements StatelessCommand {
    private static final char PREFIX = '/';
    private static final String NOT_FOUND = "Command not found. Type /help ?";

    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows list of all available commands or info about specific one. "
            + "Syntax: /help <command>";
    }

    /**
     * @param commandsArgument Map where key is a name of the command and value is Command
     * instance.
    */
    public void setCommands(final LinkedHashMap<String, Command> commandsArgument) {
        commands = commandsArgument;
    }


    @Override
    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        executeWithoutState(userId, args, responseQueue);
        return null;
    }

    @Override
    public void executeWithoutState(final int userId, final String[] args,
            final BlockingQueue<NumberedString> responseQueue) {
        String answer = "";
        if (args.length == 0) {
            answer += "Available commands:\n";
            for (Command i : commands.values()) {
                if (i instanceof InfoStub) {
                    answer += i.getName() + "\n";
                } else {
                    answer += PREFIX + i.getName() + "\n";
                }
            }
            answer += "\nType help <command> to see information about specific command";
        } else {
            answer += args[0] + ":\n";
            Command command = commands.get(args[0]);
            if (command != null) {
                answer += command.getHelp();
            } else {
                answer += NOT_FOUND;
            }
        }

        try {
            responseQueue.put(new NumberedString(userId, answer));
        } catch (InterruptedException e) {
        }
    }
}
