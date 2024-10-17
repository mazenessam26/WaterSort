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
        return getTopIndex() == 0;
    }

    // Method to check if the bottle is empty
    public boolean isEmpty() {
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

    // Updated method to check if you can pour from this bottle to another bottle
    public boolean canPourInto(Bottle targetBottle) {
        int fromTopIndex = this.getTopIndex();

        // If there's nothing to pour, or the target bottle is full, return false
        if (fromTopIndex == -1 || targetBottle.isFull()) {
            return false;
        }

        // The color of the layer we want to pour
        String colorToPour = this.layers.get(fromTopIndex);

        // Count how many matching layers are available to pour in the source bottle
        int pourableLayers = 0;
        while (fromTopIndex < capacity && this.layers.get(fromTopIndex).equals(colorToPour)) {
            pourableLayers++;
            fromTopIndex++;
        }

        // Check available space in the target bottle
        int availableSpace = targetBottle.getAvailableSpace();

        // If the target bottle is empty or the top colors match, and there is enough space for at least one layer, return true
        if (targetBottle.isEmpty()) {
            return true; // Can pour if the target bottle is empty and has enough space
        } else if (colorToPour.equals(targetBottle.layers.get(targetBottle.getTopIndex())) && availableSpace > 0) {
            // Ensure there's enough space to pour all the layers
            return pourableLayers <= availableSpace;
        }

        return false; // Cannot pour if colors don't match or if there's not enough space
    }



    // Helper method to count how many layers of the same color are present from the top
    private int countMatchingLayersFromTop(String color) {
        int count = 0;
        int index = getTopIndex();
        while (index < layers.size() && layers.get(index).equals(color)) {
            count++;
            index++;
        }
        return count;
    }


    // Updated method to transfer layers from this bottle to another bottle
    public int pourInto(Bottle targetBottle) {
        int fromTopIndex = this.getTopIndex();
        if (fromTopIndex == -1 || targetBottle.isFull()) {
            return 0; // Nothing to pour if the source is empty or target bottle is full
        }

        String colorToPour = this.layers.get(fromTopIndex);
        int pourableLayers = countMatchingLayersFromTop(colorToPour);
        int availableSpace = targetBottle.getAvailableSpace();

        // Determine how many layers can actually be poured
        int layersToPour = Math.min(pourableLayers, availableSpace);

        // Perform the pour operation
        for (int i = 0; i < layersToPour; i++) {
            // Pour only if the target bottle is either empty or the top layer matches the color being poured
            if (targetBottle.isEmpty() || targetBottle.layers.get(targetBottle.getTopIndex()).equals(colorToPour)) {
                targetBottle.addLayer(colorToPour);  // Add the layer on top
                this.removeTopLayer();  // Remove from the source bottle
            } else {
                break;  // Stop pouring if the colors don’t match (extra safeguard)
            }
        }

        return layersToPour;  // Return the number of layers poured
    }


  

    // Helper method to calculate the available space in the bottle
    private int getAvailableSpace() {
        int filledLayers = (int) layers.stream().filter(layer -> !layer.equals("e")).count();
        return capacity - filledLayers;
    }

    private void addLayer(String color) {
        for (int i = layers.size() - 1; i >= 0; i--) {
            if (layers.get(i).equals("e")) {  // Find the first empty slot from the bottom
                layers.set(i, color);         // Place the color in the empty slot
                break;
            }
        }
    }




    // Helper method to remove the topmost layer from the bottle
    private void removeTopLayer() {
        int topIndex = getTopIndex();
        if (topIndex != -1) {
            layers.set(topIndex, "e");
        }
    }

    // Convert bottle to string representation for state
    @Override
    public String toString() {
        return String.join(",", layers);
    }
}