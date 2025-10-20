package su.maibat.mon3tr.db;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DeadlineQuery extends DBQuery {
    private int id = -1;
    private Time burns = new Time(0);
    private String name = "";
    private int offset = -1;
    private int userId = -1;
    private int groupId = -1;
}
