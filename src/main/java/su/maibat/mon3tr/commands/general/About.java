package su.maibat.mon3tr.commands.general;

public final class About extends Info {
    public About() {
        info = "This bot will keep an eye on your deadlines and remind you of those that are "
            + "coming. It will try its best ;)\n\nImportant note: currently works in UTC+5 only";
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getHelp() {
        return "Display info about bot";
    }
}
