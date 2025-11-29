package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;


public abstract class InfoCommand implements Command {
    protected String info = "";

    //@Override
    public final void execute(final Chat chat) {
        chat.sendAnswer(info);
    }

    /**
     * @param newInfo new info property
    */
    public void setInfo(final String newInfo) {
        info = newInfo;
    }
}
