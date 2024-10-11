

import java.util.List;

public abstract class GenericSearch {
    // Abstract method to be implemented by the subclasses (e.g., WaterSortSearch)
    public abstract List<String> getActions(String state);  // Add state parameter

    public abstract boolean goalTest(String state);

    public abstract String solve(String initialState, String strategy, boolean visualize);
}
