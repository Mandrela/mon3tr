package su.maibat.mon3tr.commands;

public class State {
    private final int id;
    private final String[] memory;

    public State(final int stateId, final String[] memoryArray) {
        id = stateId;
        memory = memoryArray;
    }

    public State(final State state) {
        id = state.getStateId();
        memory = state.getMemory();
    }


    public int getStateId() {
        return id;
    }

    public String[] getMemory() {
        return memory;
    }
}
