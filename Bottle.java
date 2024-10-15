package code;


import java.util.*;

public class Bottle {
    List<String> layers;
    private int capacity;

    public Bottle(List<String> layers, int capacity) {
        this.layers = layers;
        this.capacity = capacity;
    }

    // Method to check if the bottle is full
    public boolean isFull() {
        // Get the top layer index, if the top index is equal to capacity - 1, it's full
        return getTopIndex() == 0;
    }

    // Method to check if the bottle is empty
    public boolean isEmpty() {
        // If the top index is -1, then the bottle is empty
        return getTopIndex() == -1;
    }

    // Method to get the top index of non-empty layers
    public int getTopIndex() {
        for (int i = 0; i < layers.size(); i++) {
            if (!layers.get(i).equals("e")) {
                return i; // Return the topmost non-empty layer index
            }
        }
        return -1; // Return -1 if the bottle is empty
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

        // If there's nothing to pour, or the target bottle is full, return false
        if (fromTopIndex == -1 || targetBottle.isFull()) {
            return false;
        }

        // If the target bottle is empty or the top colors match, return true
        if (targetBottle.isEmpty() || layers.get(fromTopIndex).equals(targetBottle.layers.get(toTopIndex))) {
            return true;
        }

        return false;
    }

    // Pour logic to transfer from this bottle to another bottle
 // Pour logic to transfer from this bottle to another bottle
    public int pourInto(Bottle targetBottle) {
        int fromTopIndex = this.getTopIndex();
        int toTopIndex = targetBottle.getTopIndex();
        int pouredLayers = 0;  // To track how many layers we poured (for the cost)

        // If source bottle is empty or target bottle is full, we can't pour
        if (fromTopIndex == -1 || targetBottle.isFull()) {
            return pouredLayers;
        }

        // The color of the layer we want to pour
        String colorToPour = layers.get(fromTopIndex);

        // Calculate how many layers can be poured (from source bottle) and how much space is available (in target bottle)
        int pourableLayers = 0;
        
        // Count how many matching layers we have in the source bottle from the top
        int index = fromTopIndex;
        while (index < capacity && layers.get(index).equals(colorToPour)) {
            pourableLayers++;
            index++;
        }

        // Count how much space is available in the target bottle
        int availableSpace = capacity - (toTopIndex + 1); // Number of empty layers in the target bottle

        // Determine the number of layers we can actually pour
        int layersToPour = Math.min(pourableLayers, availableSpace);

        // Perform the pour operation
        for (int i = 0; i < layersToPour; i++) {
            toTopIndex = (toTopIndex == -1) ? 0 : toTopIndex + 1;
            targetBottle.layers.set(toTopIndex, colorToPour);  // Pour into target bottle
            layers.set(fromTopIndex + i, "e");  // Remove from source bottle
        }

        pouredLayers = layersToPour;  // Update the cost (number of layers poured)

        return pouredLayers;  // Return the number of layers poured as the cost
    }


    // Convert bottle to string representation for state
    @Override
    public String toString() {
        return String.join(",", layers);
    }
}
