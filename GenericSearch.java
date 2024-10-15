package code;

import java.util.List;

public abstract class GenericSearch {
    // Static method signatures that will be implemented in subclasses
    public static List<String> getActions(String state) {
        return null; // Placeholder, real logic is in the subclass
    }

    public static boolean goalTest(String state) {
        return false; // Placeholder, real logic is in the subclass
    }

    public static String solve(String initialState, String strategy, boolean visualize) {
        return null; // Placeholder, real logic is in the subclass
    }
}
