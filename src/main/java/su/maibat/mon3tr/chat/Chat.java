package su.maibat.mon3tr.chat;

public interface Chat {
    String getMessage();
    String[] getAllMessages();
    void sendAnswer(String answer);

    void addMessage(String message);
    default void addMessages(String[] messages) {
        for (String message : messages) {
            addMessage(message);
        }
    }
}
