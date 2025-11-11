package su.maibat.mon3tr.chat;

public interface Chat {
    String getMessage() throws InterruptedException;
    String[] getAllMessages();
    void sendAnswer(String answer);

    boolean isEmpty();
    long getChatId();
}
