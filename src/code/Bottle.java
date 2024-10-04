package code;

import java.util.ArrayList;
import java.util.List;

public class Bottle {
    private int capacity;                 // Maximum capacity of the bottle
    private List<String> layers;          // List to store the liquid layers in the bottle

    public Bottle(int capacity) {
        this.capacity = capacity;
        this.layers = new ArrayList<>(capacity); // Initialize layers with the capacity
        for (int i = 0; i < capacity; i++) {
            layers.add("e");                // Fill the bottle with empty spaces initially
        }
    }

    /**
     * Gets the current layers in the bottle.
     * @return The layers of liquid in the bottle.
     */
    public List<String> getLayers() {
        return layers;
    }

    /**
     * Gets the current capacity of the bottle.
     * @return The capacity of the bottle.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Adds a liquid layer to the bottle.
     * @param color The color of the liquid layer to add.
     * @return true if the layer was added successfully, otherwise false.
     */
    public boolean addLayer(String color) {
        if (layers.size() < capacity) {
            layers.add(color);
            return true;
        }
        return false; // Bottle is full, cannot add more layers
    }

    /**
     * Removes the top layer from the bottle.
     * @return The color of the removed layer, or "e" if the bottle was empty.
     */
    public String removeLayer() {
        if (!layers.isEmpty()) {
            return layers.remove(layers.size() - 1); // Remove the top layer
        }
        return "e"; // Bottle is empty
    }

    /**
     * Checks if the bottle is empty.
     * @return true if the bottle is empty, otherwise false.
     */
    public boolean isEmpty() {
        return layers.stream().allMatch(layer -> layer.equals("e"));
    }

    /**
     * Returns the top layer of the bottle.
     * @return The color of the top layer, or "e" if the bottle is empty.
     */
    public String getTopLayer() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            if (!layers.get(i).equals("e")) {
                return layers.get(i);
            }
        }
        return "e"; // Bottle is empty
    }

    @Override
    public String toString() {
        return "Bottle{" +
                "capacity=" + capacity +
                ", layers=" + layers +
                '}';
    }
}
