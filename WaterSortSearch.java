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
	public static List<String> getActions(String state) {
        List<String> actions = new ArrayList<>();
        // Parse the state to create the list of bottles
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (int i = 0; i < numBottles; i++) {
            for (int j = 0; j < numBottles; j++) {
                if (i != j && bottles.get(i).canPourInto(bottles.get(j))) {
                    actions.add(new Action(i, j).toString());
                }
            }
        }
        return actions;
    }

    public static boolean goalTest(String state) {
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        for (Bottle bottle : bottles) {
            if (!bottle.isEmpty() && !bottle.isUniform()) {
                return false;
            }
        }
        return true;
    }
    
    public void setActionCosts(String costInput) {
        String[] costPairs = costInput.split(",");
        for (String pair : costPairs) {
            String[] parts = pair.split(":");
            String action = parts[0];  // e.g., "pour_0_1"
            int cost = Integer.parseInt(parts[1]);
            actionCosts.put(action, cost);
        }
    }

    
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

                if (!explored.contains(newState) && !stateInQueue(frontier, newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NO_SOLUTION;" + nodesExpanded;
    }


    // Depth-First Search (DFS)
    private static String depthFirstSearch(String initialState, boolean visualize) {
        Stack<Node> frontier = new Stack<>();
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;  // This counts all nodes expanded during the search

        Node root = new Node(initialState, null, null, 0);
        frontier.push(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.pop();
            nodesExpanded++;  // Increment the nodes expanded when a node is removed from the frontier

            if (goalTest(node.state)) {
                // Return the solution with plan, path cost, and nodes expanded
                return constructSolution(node, nodesExpanded);
            }

            explored.add(node.state);

            for (String action : getActions(node.state)) {
                String newState = applyAction(node.state, action);

                if (!explored.contains(newState) && !stateInStack(frontier, newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    System.out.println(node.pathCost);
                    frontier.push(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NO_SOLUTION;" + nodesExpanded;
    }
    
    
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

                if (!explored.contains(newState) && !stateInQueue(frontier, newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + actionCost);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState + " with path cost: " + child.pathCost);
                    }
                }
            }
        }

        return "NO_SOLUTION;" + nodesExpanded;
    }

    private static int getActionCost(String action) {
        // Retrieve the cost for the action from the map, default to 1 if not specified
        return actionCosts.getOrDefault(action, 1);
    }

    // Check if state is already in the priority queue (for UCS)
    private static boolean stateInQueue(PriorityQueue<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Function to parse the state into a list of bottles
    private static List<Bottle> parseBottles(String[] parts, int numBottles, int bottleCapacity) {
        List<Bottle> bottles = new ArrayList<>();
        for (int i = 0; i < numBottles; i++) {
            List<String> layers = Arrays.asList(parts[i + 2].split(","));
            bottles.add(new Bottle(layers, bottleCapacity));
        }
        return bottles;
    }

    private static String applyAction(String state, String action) {
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);
        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        String[] actionParts = action.split("_");
        int from = Integer.parseInt(actionParts[1]);  // Bottle to pour from
        int to = Integer.parseInt(actionParts[2]);    // Bottle to pour into
        
        // Only apply the action if it's valid
        if (bottles.get(from).canPourInto(bottles.get(to))) {
            bottles.get(from).pourInto(bottles.get(to));  // Pour from one bottle to another
        }

        // Convert the list of bottles back to state string
        return numBottles + ";" + bottleCapacity + ";" + bottlesToStateString(bottles);
    }




    // Function to convert a list of bottles back to a state string
    private static String bottlesToStateString(List<Bottle> bottles) {
        StringBuilder state = new StringBuilder();
        for (Bottle bottle : bottles) {
            state.append(bottle.toString()).append(";");
        }
        return state.toString();
    }

    // Check if state is already in the queue (for BFS)
    private static boolean stateInQueue(Queue<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Check if state is already in the stack (for DFS)
    private static boolean stateInStack(Stack<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Construct the solution from the final node
    private static String constructSolution(Node node, int nodesExpanded) {
        StringBuilder solution = new StringBuilder();
        int pathCost = node.pathCost;  // Take the path cost from the final node
        int nodesInPath = 0;  // Track the number of nodes in the solution path

        while (node != null && node.action != null) {
            solution.insert(0, node.action + ",");
            node = node.parent;
            nodesInPath++;  // Count each node in the solution path
        }

        // Return the solution in the format: "plan; pathCost; nodesExpanded"
        return solution.toString() + ";" + pathCost + ";" + nodesExpanded;
    }
    
    
    
 // Iterative Deepening Search (IDS)
    private static String iterativeDeepeningSearch(String initialState, boolean visualize) {
        int depthLimit = 0;  // Start with depth limit 0
        int nodesExpanded = 0;

        while (true) {
            // Call depth-limited search
            String result = depthLimitedSearch(initialState, depthLimit, visualize, nodesExpanded);
            if (!result.equals("CUTOFF")) {
                // If we get a solution or no solution, return the result
                return result;
            }
            depthLimit++;  // Increase depth limit
        }
    }

    // Depth-Limited Search for IDS
    private static String depthLimitedSearch(String initialState, int limit, boolean visualize, int nodesExpanded) {
        Stack<Node> frontier = new Stack<>();
        Set<String> explored = new HashSet<>();

        Node root = new Node(initialState, null, null, 0);
        frontier.push(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.pop();
            nodesExpanded++;  // Increment nodes expanded

            if (goalTest(node.state)) {
                return constructSolution(node, nodesExpanded);  // Return solution if found
            }

            if (node.pathCost < limit) {  // Only expand nodes within depth limit
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

        return "CUTOFF";  // Return CUTOFF if the search reached the limit without finding a solution
    }
    
    
    
    
    
    
    
 // Heuristic function with two different heuristics
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

                if (!explored.contains(newState) && !stateInQueue(frontier, newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NO_SOLUTION;" + nodesExpanded;
    }

    
    
    
    
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

                if (!explored.contains(newState) && !stateInQueue(frontier, newState)) {
                    Node child = new Node(newState, node, action, node.pathCost + 1);
                    frontier.add(child);

                    if (visualize) {
                        System.out.println("Current State: " + newState);
                    }
                }
            }
        }

        return "NO_SOLUTION;" + nodesExpanded;
    }

    // A* Search total cost function: f(n) = g(n) + h(n)
    private static int totalCost(Node node, int heuristicOption) {
        return node.pathCost + heuristic(node, heuristicOption);
    }

    
    
    
    
    
    

}
