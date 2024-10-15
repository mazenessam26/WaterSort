package code;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for the initial state, strategy, and visualization
        System.out.println("Enter the initial state (e.g., '5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e;'):");
        String initialState = scanner.nextLine();

        System.out.println("Enter the search strategy (BF, DF, UC, ID, GR1, GR2, AS1, AS2):");  // Added ID for IDS
        String strategy = scanner.nextLine();

        /*System.out.println("Enter the cost for each action (e.g., 'pour_0_1:2,pour_0_2:3,pour_1_2:1,pour_1_3:4'):");
        String costInput = scanner.nextLine();

        // Parse the input into a map with validation
        Map<String, Integer> actionCosts = new HashMap<>();
        for (String pair : costInput.split(",")) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
                    actionCosts.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid cost format for: " + pair);
                }
            } else {
                System.out.println("Invalid input format for pair: " + pair);
            }
        }*/

        // Ensure correct input for visualization
        boolean visualize = false;
        while (true) {
            System.out.println("Do you want to visualize the process? (true/false):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("true")) {
                visualize = true;
                break;
            } else if (input.equalsIgnoreCase("false")) {
                visualize = false;
                break;
            } else {
                System.out.println("Please enter either 'true' or 'false'.");
            }
        }

        // Create an instance of WaterSortSearch and solve the problem
        WaterSortSearch waterSortSearch = new WaterSortSearch();
        String result = waterSortSearch.solve(initialState, strategy, visualize);  // Solve with the chosen strategy

        // Display the result
        System.out.println("Result: " + result);
    }
}
