package su.maibat.mon3tr.bot;

import static su.maibat.mon3tr.Main.ERROR;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.StatelessCommand;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;


/** Super cool bot logic (<i>cannot buttons</i>). */
public final class Bot implements BotBackend {
    private static final char COMMAND_PREFIX = '/';

    private final StatelessCommand defaultCommand;
    private final StatelessCommand registerCommand;
    private final Map<String, Command> commands;
    /** Holds State objects for commands. Theoretically can be upgraded to SoftReferenceMap. */
    private final Map<Integer, State> stateMap = new HashMap<>();

    private final DataBaseLinker db;
    private final BlockingQueue<NumberedString> responseQueue;


    /**
     * @param dataBase Source of relaxation.
     * @param defaultCommandArg This command is called whenever dunno what to do.
     * @param registerCommandArg This command is called whenever unregistered user (see process).
     * @param commandsMap Map of all available commands. Key represents name of the command by
     * which it will be called through value reference.
     * @param responseQueueArg Will put result of the work in this queue.
     * @throws IllegalArgumentException If one of the arguments is null. Won't tell ya which one
     * of them though
     * @see su.maibat.mon3tr.bot.Bot#process(int, String) process
     */
    public Bot(final DataBaseLinker dataBase,
            final StatelessCommand defaultCommandArg, final StatelessCommand registerCommandArg,
            final Map<String, Command> commandsMap,
            final BlockingQueue<NumberedString> responseQueueArg
    ) {
        if (dataBase == null || defaultCommandArg == null || registerCommandArg == null
            || commandsMap == null || responseQueueArg == null) {
            throw new IllegalArgumentException("One of the arguments is null");
        }
        db = dataBase;
        defaultCommand = defaultCommandArg;
        registerCommand = registerCommandArg;
        commands = Collections.unmodifiableMap(commandsMap);
        responseQueue = responseQueueArg;
    }


    /**
     * Processes commands.
     * @param userId Integer representation of user. Bot assumes that users with id < 0
     * are not registered yet without any additional checks in database. Be careful and use
     * this mechanic only if you aren't drunk.
     * @param commandString String containing command for execution with args.
     * @throws BotException Currently not used.
     */
    public void process(final int userId, final String commandString) throws BotException {
        if (userId < 0 || !db.checkUserExists(userId)) { // others
            registerCommand.executeWithoutState(userId, commandString.split(" "), responseQueue);

        } else { // all legitemate users that do exist in database
            String[] tokens = commandString.split(" ");
            if (tokens[0].charAt(0) == COMMAND_PREFIX) {

                executeCommand(commands.getOrDefault(tokens[0].substring(1), defaultCommand),
                    userId, Arrays.copyOfRange(tokens, 1, tokens.length), null);

            } else {
                State currentState = stateMap.get(userId);
                if (currentState != null) {
                    assert currentState.getOwner() != null : "Malformed state in map (null owner)";
                    executeCommand(currentState.getOwner(), userId, tokens, currentState);
                } else {
                    defaultCommand.executeWithoutState(userId, tokens, responseQueue);
                }
            }
        }
    }

    /** Safely execute command, made for not repeating.
     * @param commandToExecute Yes.
     * @param userId Yes.
     * @param arguments Yes.
     * @param currentState Yes. Can be null.
     */
    private void executeCommand(final Command commandToExecute, final int userId,
            final String[] arguments, final State currentState) {
        try {
            stateMap.put(userId, commandToExecute.execute(userId, arguments,
                currentState, responseQueue));
        } catch (CommandException e) {
            System.err.println(ERROR + "Error in executable command "
                + commandToExecute.getName() + " with message: " + e.getMessage());
        }
    }


    /**
     * @return Returns command identificator.
     */
    public char getCommandPrefix() {
        return COMMAND_PREFIX;
    }
}
