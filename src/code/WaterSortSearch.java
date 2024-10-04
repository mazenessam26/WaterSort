package code;

import java.util.*;


public class WaterSortSearch extends GenericSearch {
    private List<Bottle> bottles;

    public WaterSortSearch() {
        bottles = new ArrayList<>();
    }

    private void initializeState(String initialStateString) {
        String[] parts = initialStateString.split(";");
        int numberOfBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        for (int i = 2; i < parts.length; i++) {
            Bottle bottle = new Bottle(bottleCapacity);
            String[] layers = parts[i].split(",");
            for (String color : layers) {
                if (!color.equals("e")) {
                    bottle.addLayer(color);
                }
            }
            bottles.add(bottle);
        }
    }

    @Override
    public boolean isGoalState(Object state) {
        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty() && bottle.getLayers().stream().distinct().count() > 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Object> getSuccessors(Object state) {
        List<Object> successors = new ArrayList<>();
        // Implement logic to generate valid pour actions
        return successors;
    }

    @Override
    public Object applyAction(Object state, String action) {
        // Implement the logic to apply the pouring action to the bottles
        return state; // Return the new state after the action
    }

    @Override
    public double getPathCost(List<String> path) {
        // Assuming each action has a cost of 1
        return path.size(); // Return the total cost as the number of actions
    }

    public String solve(String initialState, String strategy, boolean visualize) {
        initializeState(initialState);
        // Call the appropriate search method based on the strategy
        return "Solution logic not implemented";
    }
}
