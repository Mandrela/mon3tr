package su.maibat.mon3tr.commands;

public final class AuthorsCommand extends InfoCommand {
    public AuthorsCommand() {
        info = "The great Mandrela, beloved member of human race, father to all sons";
    }

    @Override
    public String getName() {
        return "authors";
    }

    @Override
    public String getHelp() {
        return "Show info about bot' authors";
    }
}
