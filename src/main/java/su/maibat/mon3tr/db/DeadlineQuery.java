package su.maibat.mon3tr.db;

import java.sql.Time;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeadlineQuery extends DBQuery {
    private String name = "";
    private Time burnTime = new Time(0);
    private Timestamp offset = new Timestamp(0);
    private int userId = 0;
    private int groupId = 0;

    public DeadlineQuery() {
        super();
    }

    /**
     * @param idArg
     * @param deadlineName Display name of the deadline
     * @param burnAtTime Time at wich deadline will be burned
     * @param triggerOffset Relative to burnAtTime. Deadlines between burnAtTime and offset are
     * considered burning
     * @param ownerUserId Id of user to notify
     * @param ownerGroupId Id of group to notify
     */
    public DeadlineQuery(final int idArg, final String deadlineName, final Time burnAtTime,
        final Timestamp triggerOffset, final int ownerUserId, final int ownerGroupId) {
            super(idArg);
            name = deadlineName;
            burnTime = burnAtTime;
            offset = triggerOffset;
            userId = ownerUserId;
            groupId = ownerGroupId;
    }
}
