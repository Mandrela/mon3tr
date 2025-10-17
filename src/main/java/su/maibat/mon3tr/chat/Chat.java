package su.maibat.mon3tr.chat;

public interface Chat {
    String getMessage();
    String[] getAllMessages();
    void sendAnswer(String answer);

    void addMessage(String message);
}
