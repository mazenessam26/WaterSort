package code;



import java.util.List;

public abstract class GenericSearch {
    // Abstract method to be implemented by the subclasses (e.g., WaterSortSearch)
    public static  List<String> getActions(String state) {
		return null;
	}  // Add state parameter

    public static  boolean goalTest(String state) {
		return false;
	}

    public static  String solve(String initialState, String strategy, boolean visualize) {
		return null;
	}
}
