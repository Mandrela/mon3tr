package su.maibat.mon3tr.commands.general;

public final class Authors extends Info {
    public Authors() {
        info = "The great Mandrela, beloved member of human race, father to all sons.\n"
            + "You can contact me through nearest shrine.\n"
            + "Don't forget to offer a beer in exchange";
    }

    @Override
    public String getName() {
        return "authors";
    }

    @Override
    public String getHelp() {
        return "Display authors' info";
    }
}
