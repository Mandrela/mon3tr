package su.maibat.mon3tr;

public final class AuthorsCommand extends InfoCommand {
    public AuthorsCommand() {
        info = "The great Mandrela, beloved member of human race, father to all sons";
    }

    public String getName() {
        return "authors";
    }

    public String getHelp() {
        return "Show info about bot' authors";
    }
}
