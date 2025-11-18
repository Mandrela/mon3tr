package su.maibat.mon3tr.chat;

public interface MessageSink {
    void addMessage(String message);
    default void addMessages(String[] messages) {
        for (String message : messages) {
            addMessage(message);
        }
    }
    void interrupt();
}
