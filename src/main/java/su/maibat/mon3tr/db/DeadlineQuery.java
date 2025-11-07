package su.maibat.mon3tr.db;

import java.math.BigDecimal;

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

    public String getName() {
        return name;
    }
    public void setName(final String arg) {
        this.name = arg;
    }

    public BigDecimal getBurnTime() {
        return burnTime;
    }
    public void setBurnTime(final BigDecimal arg) {
        this.burnTime = arg;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(final int arg) {
        this.userId = arg;
    }

    public BigDecimal getOffset() {
        return offset;
    }
}
