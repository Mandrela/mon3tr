package su.maibat.mon3tr.chat;

public interface Chat {
    /**
     * @return One message from dialog with user. Will wait if no messages available right away
     * @throws InterruptedException If wait was interrupted
     */
    String getMessage() throws InterruptedException;
    /**
     * @param message Message that will be sent before recieving answer
     * @return One message from dialog with user. Will wait if no messages available right away
     * @throws InterruptedException If wait was interrupted
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
