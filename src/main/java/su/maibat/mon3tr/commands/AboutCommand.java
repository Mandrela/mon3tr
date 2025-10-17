package su.maibat.mon3tr.commands;

public final class AboutCommand extends InfoCommand {
    public AboutCommand() {
        info = "This bot will keep an eye on your deadlines and remind you of those that are "
            + "coming, but it doesn't know how to do that yet, but it will try its best.";
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getHelp() {
        return "Show information about bot";
    }
}
