package su.maibat.mon3tr.commands;

import java.util.LinkedHashMap;

import su.maibat.mon3tr.chat.Chat;


public final class HelpCommand implements Command {
    private static final char PREFIX = '/';
    private static final String NOT_FOUND = "Command not found";

    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows list of all available commands or info about specific one";
    }

    /**
     * @param commandsArgument Map where key is a name of the command and value is Command
     * instance.
    */
    public void setCommands(final LinkedHashMap<String, Command> commandsArgument) {
        commands = commandsArgument;
    }


    @Override
    public void execute(final Chat chat) {
        String[] args = chat.getAllMessages();
        String answer = "";
        if (args.length == 0) {
            answer += "Available commands:\n\n";
            for (String i : commands.keySet()) {
                answer += PREFIX + i + "\n";
            }
            answer += "\nType help <command> to see information about specific command";
        } else {
            for (String arg : args) { // Suppose args - one worded command names
                answer += arg + " --- ";
                if (commands.containsKey(arg)) {
                    answer += commands.get(arg).getHelp();
                } else {
                    answer += NOT_FOUND;
                }
                answer += "\n";
            }
        }

        chat.sendAnswer(answer);
    }
}
