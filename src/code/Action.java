package code;

public class Action {
    private int fromBottle; // The index of the source bottle
    private int toBottle;   // The index of the destination bottle

    public Action(int fromBottle, int toBottle) {
        this.fromBottle = fromBottle;
        this.toBottle = toBottle;
    }

    /**
     * Gets the index of the source bottle.
     * @return The index of the source bottle.
     */
    public int getFromBottle() {
        return fromBottle;
    }

    /**
     * Gets the index of the destination bottle.
     * @return The index of the destination bottle.
     */
    public int getToBottle() {
        return toBottle;
    }

    @Override
    public String toString() {
        return "Action{" +
                "fromBottle=" + fromBottle +
                ", toBottle=" + toBottle +
                '}';
    }
}
