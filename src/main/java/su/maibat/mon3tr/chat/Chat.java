package su.maibat.mon3tr.chat;

public interface Chat {
    String getMessage() throws InterruptedException;
    /**
     * @param message Message that will be sent before recieving answer
     */
    default String getMessage(String message) throws InterruptedException {
        sendAnswer(message);
        return getMessage();
    }
    String[] getAllMessages();
    void sendAnswer(String answer);

    boolean isEmpty();
    long getChatId();
}
