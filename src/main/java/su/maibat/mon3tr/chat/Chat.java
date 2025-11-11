package su.maibat.mon3tr.chat;

public interface Chat {
    String getMessage();
    /**
     * @param message Message that will be sent before recieving answer
     */
    default String getMessage(String message) {
        sendAnswer(message);
        return getMessage();
    }
    String[] getAllMessages();
    void sendAnswer(String answer);

    boolean isEmpty();
    long getChatId();
}
