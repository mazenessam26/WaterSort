package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {
    private static Map<String, Integer> actionCosts;

    public WaterSortSearch(Map<String, Integer> actionCosts) {
        if (actionCosts != null) {
            WaterSortSearch.actionCosts = actionCosts;
        } else {
            WaterSortSearch.actionCosts = new HashMap<>();  // Initialize with empty map if null is passed
        }
    }

 // Get all valid actions based on the current state
    public static List<String> getActions(String state) {
        List<String> actions = new ArrayList<>();
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (int i = 0; i < numBottles; i++) {
            for (int j = 0; j < numBottles; j++) {
                if (i != j && bottles.get(i).canPourInto(bottles.get(j))) {
                    String action = new Action(i, j).toString();
                    actions.add(action);
                    System.out.println("Valid Action: " + action);  // Debug output
                }
            }
        }
        return actions;
    }



    // Goal state test: check if all bottles are uniform or empty
    public static boolean goalTest(String state) {
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty() && !bottle.isUniform()) {
                System.out.println("Bottle not uniform: " + bottle);
                return false;
            }
        }
        // Check that all colors are in complete bottles (not spread across multiple bottles)
        Set<String> colorsInSingleBottle = new HashSet<>();
        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty()) {
                String topColor = bottle.layers.get(bottle.getTopIndex());
                if (!colorsInSingleBottle.add(topColor)) {
                    System.out.println("Color " + topColor + " is split across bottles.");
                    return false;
                }
            }
        }
        return true;
    }


 // Depth-First Search (DFS) implementation with backtracking prevention
    private static String depthFirstSearch(String initialState, boolean visualize) {
        Stack<Node> frontier = new Stack<>();
        Set<String> explored = new HashSet<>();  // This set tracks fully explored states
        int nodesExpanded = 0;

        Node root = new Node(initialState, null, null, 0);
        frontier.push(root);
        explored.add(root.state);  // Add the root state to explored immediately

        while (!frontier.isEmpty()) {
            Node node = frontier.pop();
            nodesExpanded++;

            // Check if the goal is achieved
            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);
            }

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);

                // Prevent backtracking by checking if the action is the reverse of the previous one
                if (node.action != null && isReverseAction(node.action, action)) {
                    continue;  // Skip this action if it reverses the previous one
                }

                // Only proceed if the state hasn't been explored yet
                if (!explored.contains(newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    explored.add(newState);  // Mark the state as explored immediately
                    frontier.push(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NOSOLUTION";
    }

    // Helper function to check if the action reverses the previous one
    private static boolean isReverseAction(String prevAction, String currentAction) {
        String[] prevParts = prevAction.split("_");
        String[] currParts = currentAction.split("_");

        // Check if the current action reverses the previous action
        return prevParts[1].equals(currParts[2]) && prevParts[2].equals(currParts[1]);
    }


 // Iterative Deepening Search (IDS) implementation
    private static String iterativeDeepeningSearch(String initialState, boolean visualize) {
        int depthLimit = 0;
        int nodesExpanded = 0;

        while (true) {
            String result = depthLimitedSearch(initialState, depthLimit, visualize, nodesExpanded);
            if (!result.equals("CUTOFF")) {
                return result;
            }
            depthLimit++;  // Increase depth limit and try again
        }
    }

    // Depth-Limited Search (DLS) used in IDS
    private static String depthLimitedSearch(String initialState, int limit, boolean visualize, int nodesExpanded) {
        Stack<Node> frontier = new Stack<>();
        Set<String> explored = new HashSet<>();

        Node root = new Node(initialState, null, null, 0);
        frontier.push(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.pop();
            nodesExpanded++;

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);  // Return solution if goal is found
            }

            if (node.pathCost < limit) {  // Limit search depth
                explored.add(node.state);

                for (String action : getActions(node.state)) {
                    String newState = applyAction(node.state, action);
                    if (!explored.contains(newState) && !stateInStack(frontier, newState)) {
                        Node child = new Node(newState, node, action, node.pathCost + 1);
                        frontier.push(child);

                        if (visualize) {
                            System.out.println("Current State: " + newState);
                        }
                    }
                }
            }
        }

        return "CUTOFF";  // Cutoff if depth limit is reached without a solution
    }

    // Solve method that handles different strategies
    public static String solve(String initialState, String strategy, boolean visualize) {
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String result = "";  // Result from the selected search algorithm

        if (strategy.equals("BF")) {
            result = breadthFirstSearch(initialState, visualize);
        } else if (strategy.equals("DF")) {
            result = depthFirstSearch(initialState, visualize);
        } else if (strategy.equals("UC")) {
            result = uniformCostSearch(initialState, visualize);
        } else if (strategy.equals("ID")) {
            result = iterativeDeepeningSearch(initialState, visualize);  // Add IDS support
        } else if (strategy.startsWith("GR")) {
            int heuristicOption = Integer.parseInt(strategy.substring(2));  // Extract heuristic option (GR1 or GR2)
            result = greedySearch(initialState, visualize, heuristicOption);
        } else if (strategy.startsWith("AS")) {
            int heuristicOption = Integer.parseInt(strategy.substring(2));  // Extract heuristic option (AS1 or AS2)
            result = aStarSearch(initialState, visualize, heuristicOption);
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Memory used: " + (endMemory - startMemory) + " bytes");

        return result;
    }

    // Breadth-First Search (BFS)
    private static String breadthFirstSearch(String initialState, boolean visualize) {
        Queue<Node> frontier = new LinkedList<>();
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;  // Count of expanded nodes

        Node root = new Node(initialState, null, null, 0);
        frontier.add(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            nodesExpanded++;

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);
            }

            explored.add(node.state);

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);

                if (!explored.contains(newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NOSOLUTION";
    }

    // Uniform-Cost Search (UCS)
    private static String uniformCostSearch(String initialState, boolean visualize) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(node -> node.pathCost));
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;

        Node root = new Node(initialState, null, null, 0);
        frontier.add(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            nodesExpanded++;

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);
            }

            explored.add(node.state);

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);
                int actionCost = getActionCost(action);

                if (!explored.contains(newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + actionCost);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState + " with path cost: " + child.pathCost);
                    }
                }
            }
        }

        return "NOSOLUTION";
    }
 // Method to retrieve the cost of a specific action
    private static int getActionCost(String action) {
        // Return the cost from the map, if not found default to 1
        return actionCosts.getOrDefault(action, 1);
    }

    // Greedy Search
    private static String greedySearch(String initialState, boolean visualize, int heuristicOption) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(node -> heuristic(node, heuristicOption)));
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;

        Node root = new Node(initialState, null, null, 0);
        frontier.add(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            nodesExpanded++;

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);
            }

            explored.add(node.state);

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);

                if (!explored.contains(newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NOSOLUTION";
    }

    // A* Search
    private static String aStarSearch(String initialState, boolean visualize, int heuristicOption) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(node -> totalCost(node, heuristicOption)));
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;

        Node root = new Node(initialState, null, null, 0);
        frontier.add(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            nodesExpanded++;

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);
            }

            explored.add(node.state);

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);

                if (!explored.contains(newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NOSOLUTION";
    }

    // Helper method for heuristic function
    private static int heuristic(Node node, int heuristicOption) {
        if (heuristicOption == 1) {
            return heuristic1(node);  // Heuristic 1: Count unsorted bottles
        } else if (heuristicOption == 2) {
            return heuristic2(node);  // Heuristic 2: Count misplaced layers
        }
        return Integer.MAX_VALUE;  // Default: Should never happen
    }

    // Heuristic 1: Number of unsorted bottles
    private static int heuristic1(Node node) {
        int unsortedBottles = 0;
        String[] parts = node.state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty() && !bottle.isUniform()) {
                unsortedBottles++;  // Count bottles that are not uniform (contain more than one color)
            }
        }

        return unsortedBottles;
    }

    // Heuristic 2: Number of misplaced layers
    private static int heuristic2(Node node) {
        int misplacedLayers = 0;
        String[] parts = node.state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty()) {
                String topColor = bottle.layers.get(bottle.getTopIndex());  // Get the top color in the bottle
                for (String layer : bottle.layers) {
                    if (!layer.equals("e") && !layer.equals(topColor)) {
                        misplacedLayers++;  // Count layers that don't match the top color
                    }
                }
            }
        }

        return misplacedLayers;
    }

    // A* total cost function: f(n) = g(n) + h(n)
    private static int totalCost(Node node, int heuristicOption) {
        return node.pathCost + heuristic(node, heuristicOption);
    }
 // Method to apply an action to the current state
    private static String applyAction(String state, String action) {
        System.out.println("Applying action: " + action + " on state: " + state);
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);
        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        String[] actionParts = action.split("_");
        int from = Integer.parseInt(actionParts[1]);
        int to = Integer.parseInt(actionParts[2]);

        // Check if the action is valid before applying
        if (bottles.get(from).canPourInto(bottles.get(to))) {
            bottles.get(from).pourInto(bottles.get(to));
        } else {
            System.out.println("Invalid action: " + action);
        }

        // Rebuild the state after applying the action
        String newState = numBottles + ";" + bottleCapacity + ";" + bottlesToStateString(bottles);
        System.out.println("New State after action " + action + ": " + newState);  // Debugging output
        return newState;
    }




 // Convert the list of bottles back into a string format
    private static String bottlesToStateString(List<Bottle> bottles) {
        StringBuilder state = new StringBuilder();
        for (Bottle bottle : bottles) {
            state.append(bottle.toString()).append(";");
        }
        return state.toString();
    }


    private static List<Bottle> parseBottles(String[] parts, int numBottles, int bottleCapacity) {
        List<Bottle> bottles = new ArrayList<>();
        for (int i = 0; i < numBottles; i++) {
            List<String> layers = Arrays.asList(parts[i + 2].split(","));
            bottles.add(new Bottle(layers, bottleCapacity));
        }
        return bottles;
    }

 // Helper method to check if a state is already in the frontier stack
    private static boolean stateInStack(Stack<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }


 // Helper method to construct the solution from the final node
    private static String constructSolution(Node node, int nodesExpanded) {
        StringBuilder solution = new StringBuilder();
        int pathCost = node.pathCost;  // Take the path cost from the final node

        while (node != null && node.action != null) {
            solution.insert(0, node.action + (solution.length() > 0 ? "," : "")); // Add comma only if solution is not empty
            node = node.parent;
        }

        return solution.toString() + ";" + pathCost + ";" + nodesExpanded;
    }
}

