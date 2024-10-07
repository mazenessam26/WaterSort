
import java.util.*;

public class Bottle {

    private List<String> layers;
    private int capacity;

    public Bottle(List<String> layers, int capacity) {
        this.layers = layers;
        this.capacity = capacity;
    }

    // Method to check if the bottle is full
    public boolean isFull() {
        return getTopIndex() == capacity - 1;
    }

    // Method to check if the bottle is empty
    public boolean isEmpty() {
        return getTopIndex() == -1;
    }

    // Method to get the top index of non-empty layers
    public int getTopIndex() {
        for (int i = 0; i < layers.size(); i++) {
            if (!layers.get(i).equals("e")) {
                return i;
            }
        }
        return -1;
    }

    // Method to check if all non-empty layers in the bottle are the same color
    public boolean isUniform() {
        String color = null;
        for (String layer : layers) {
            if (!layer.equals("e")) {  // Ignore empty slots
                if (color == null) {
                    color = layer;
                } else if (!color.equals(layer)) {
                    return false;  // Found a different color
                }
            }
        }
        return true;
    }

    // Method to check if you can pour from this bottle to another bottle
    public boolean canPourInto(Bottle targetBottle) {
        int fromTopIndex = this.getTopIndex();
        int toTopIndex = targetBottle.getTopIndex();

        // If there's nothing to pour, or target is full, return false
        if (fromTopIndex == -1 || targetBottle.isFull()) {
            return false;
        }

        // If the target is empty or the colors match, return true
        if (targetBottle.isEmpty() || layers.get(fromTopIndex).equals(targetBottle.layers.get(toTopIndex))) {
            return true;
        }

        return false;
    }

    // Pour logic to transfer from this bottle to another bottle
    public void pourInto(Bottle targetBottle) {
        int fromTopIndex = this.getTopIndex();
        int toTopIndex = targetBottle.getTopIndex();

        while (fromTopIndex != -1 && toTopIndex + 1 < capacity &&
                (toTopIndex == -1 || layers.get(fromTopIndex).equals(targetBottle.layers.get(toTopIndex)))) {
            toTopIndex = (toTopIndex == -1) ? 0 : toTopIndex + 1;
            targetBottle.layers.set(toTopIndex, layers.get(fromTopIndex));
            layers.set(fromTopIndex, "e");
            fromTopIndex = getTopIndex();
        }
    }

    // Convert bottle to string representation for state
    @Override
    public String toString() {
        return String.join(",", layers);
    }
}
