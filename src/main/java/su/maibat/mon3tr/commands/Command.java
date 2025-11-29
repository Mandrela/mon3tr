package su.maibat.mon3tr.commands;

// import su.maibat.mon3tr.chat.Chat;

// they assume, that user exists
public interface Command {
    String getName();
    String getHelp();

    //State execute(int userId, String[] args, State currentState) throws CommandException;
}
