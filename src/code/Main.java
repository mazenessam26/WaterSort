package code;
import code.WaterSortSearch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // 1. Parse input arguments (e.g., initialState, strategy, visualize)
        String initialState = args[0];
        String strategy = args[1];  // BF, DF, etc.
        boolean visualize = Boolean.parseBoolean(args[2]);

        // 2. Instantiate the WaterSortSearch object
        WaterSortSearch searchAgent = new WaterSortSearch();

        // 3. Call the solve method with the input parameters
        String result = searchAgent.solve(initialState, strategy, visualize);

        // 4. Display the result
        if (result.equals("NOSOLUTION")) {
            System.out.println("NOSOLUTION");
        } else {
            // Plan, Path Cost, Nodes Expanded
            String[] resultParts = result.split(";");
            System.out.println("Plan: " + resultParts[0]);
            System.out.println("Path Cost: " + resultParts[1]);
            System.out.println("Nodes Expanded: " + resultParts[2]);
        }
    }
}
