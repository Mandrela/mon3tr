package su.maibat.mon3tr.db;

public final class DeadlineQuery extends DBQuery {
    private String name = "";
    private long burnTime = 0;
    private long offset = 0;
    private int userId = 0;
    private int state = 0;
    // private int groupId = 0;

    public DeadlineQuery() {
        super();
        updateState();
    }

    /**
     * @param idArg
     * @param deadlineName Display name of the deadline
     * @param burnAtTime Time at wich deadline will be burned
     * @param triggerOffset Relative to burnAtTime. Deadlines between burnAtTime and offset are
     * considered burning
     * @param ownerUserId Id of user to notify
     */
    public DeadlineQuery(final int idArg, final String deadlineName, final long burnAtTime,
            final long triggerOffset, final int ownerUserId) {
        super(idArg);
        name = deadlineName;
        burnTime = burnAtTime;
        offset = triggerOffset;
        userId = ownerUserId;
        updateState();
    }

    private void updateState() {
        long currentTime = System.currentTimeMillis();
        System.out.println("Burn: " + burnTime);
        System.out.println("Current: " + currentTime);
        if (currentTime > burnTime) {
            state = -1;
        } else if (currentTime + offset > burnTime) {
            state = 1;
        } else {
            state = 0;
        }
    }

    public String getName() {
        return name;
    }
    public void setName(final String arg) {
        name = arg;
    }

    public long getBurnTime() {
        return burnTime;
    }
    public void setBurnTime(final long arg) {
        burnTime = arg;
        updateState();
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(final int arg) {
        userId = arg;
    }

    public long getOffset() {
        return offset;
    }
    public void setOffset(final long arg) {
        offset = arg;
        updateState();
    }

    public boolean isBurning() {
        return state == 1;
    }

    public boolean isDead() {
        return state == -1;
    }
}
