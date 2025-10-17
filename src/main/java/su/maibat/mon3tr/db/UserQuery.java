package su.maibat.mon3tr.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserQuery {
    private int id = -1;
    private long chatId = -1;
}
