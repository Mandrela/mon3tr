package su.maibat.mon3tr.db;



public final class UserQuery extends DBQuery {
    private static final int STANDARD_LIMIT = 32;

    private long chatId = 0;
    private int limit = STANDARD_LIMIT;

    // For future use
    private boolean hasPaidSubscribeForWeatherNews = false;
    @SuppressWarnings("unused")
    private boolean sendNews;
    @SuppressWarnings("unused")
    private int remindStrategy;

    @SuppressWarnings("unused")
    private boolean concurrent;
    @SuppressWarnings("unused")
    private int burnedDeadlines;
    @SuppressWarnings("unused")
    private int completedDeadlines;
    @SuppressWarnings("unused")
    private String name;

    private int[] membership;


    public UserQuery() {
        super();
    }

    public UserQuery(final int id, final long targetChatId, final int[] groups) {
        super(id);
        chatId = targetChatId;
        membership = groups;
    }


    public long getChatId() {
        return chatId;
    }

    public void setChatId(final long arg) {
        this.chatId = arg;
    }


    public int getLimit() {
        return limit;
    }
    public void setLimit(final int arg) {
        this.limit = arg;
    }


    public boolean isHasPaidSubscribeForWeatherNews() {
        return hasPaidSubscribeForWeatherNews;
    }


    public int[] getMembership() {
        return membership;
    }

    public void setMembership(final int[] newMembership) {
        membership = newMembership;
    }
}
