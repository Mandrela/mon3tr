package su.maibat.mon3tr.db;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeadlineQuery extends DBQuery {
    private String name = "";
    private BigDecimal burnTime = new BigDecimal(0);
    private BigDecimal offset = new BigDecimal(0);
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
     */
    public DeadlineQuery(final int idArg, final String deadlineName, final BigDecimal burnAtTime,
        final BigDecimal triggerOffset, final int ownerUserId) {
            super(idArg);
            name = deadlineName;
            burnTime = burnAtTime;
            offset = triggerOffset;
            userId = ownerUserId;
    }
}
