package su.maibat.mon3tr.db;



public class UserQuery extends DBQuery {
    private static final int STANDARD_LIMIT = 32;

    private long chatId = 0;
    private int limit = STANDARD_LIMIT;
    private boolean hasPaidSubscribeForWeatherNews = false;

    public UserQuery() {
        super();
    }


    /**
     * @param idArg
     * @param targetChatId
     */
    public UserQuery(final int idArg, final long targetChatId) {
        super(idArg);
        chatId = targetChatId;
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

    @Override
    public int getId() {
        return super.getId();
    }

    public boolean isHasPaidSubscribeForWeatherNews() {
        return hasPaidSubscribeForWeatherNews;
    }
}
