package su.maibat.mon3tr.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
