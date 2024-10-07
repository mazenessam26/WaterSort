

public class Action {
    private int fromBottleIndex;
    private int toBottleIndex;

    public Action(int from, int to) {
        this.fromBottleIndex = from;
        this.toBottleIndex = to;
    }

    public int getFromBottleIndex() {
        return fromBottleIndex;
    }

    public int getToBottleIndex() {
        return toBottleIndex;
    }

    @Override
    public String toString() {
        return "pour_" + fromBottleIndex + "_" + toBottleIndex;
    }
}
