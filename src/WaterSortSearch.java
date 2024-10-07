
import java.util.*;

public class WaterSortSearch extends GenericSearch {
	private Map<String, Integer> actionCosts;

    public WaterSortSearch(Map<String, Integer> actionCosts) {
		// TODO Auto-generated constructor stub
    	this.actionCosts = actionCosts;
	}

	@Override
    public List<String> getActions(String state) {
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

    @Override
    public boolean goalTest(String state) {
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

    @Override
    public String solve(String initialState, String strategy, boolean visualize) {
        if (strategy.equals("BF")) {
            return breadthFirstSearch(initialState, visualize);
        } else if (strategy.equals("DF")) {
            return depthFirstSearch(initialState, visualize);
        }
        else if (strategy.equals("UC")) {
        return uniformCostSearch(initialState, visualize);  // Call UCS method
    }
        return "Invalid strategy";
    }

    // Breadth-First Search (BFS)
    private String breadthFirstSearch(String initialState, boolean visualize) {
        Queue<Node> frontier = new LinkedList<>();
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;  // This counts all nodes expanded during the search

        Node root = new Node(initialState, null, null, 0);
        frontier.add(root);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            nodesExpanded++;  // Increment the nodes expanded when a node is removed from the frontier

            if (goalTest(node.state)) {
                // Return the solution with plan, path cost, and nodes expanded
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
    private String depthFirstSearch(String initialState, boolean visualize) {
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
    
    
    private String uniformCostSearch(String initialState, boolean visualize) {
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

    private int getActionCost(String action) {
        // Retrieve the cost for the action from the map, default to 1 if not specified
        return actionCosts.getOrDefault(action, 1);
    }

    // Check if state is already in the priority queue (for UCS)
    private boolean stateInQueue(PriorityQueue<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Function to parse the state into a list of bottles
    private List<Bottle> parseBottles(String[] parts, int numBottles, int bottleCapacity) {
        List<Bottle> bottles = new ArrayList<>();
        for (int i = 0; i < numBottles; i++) {
            List<String> layers = Arrays.asList(parts[i + 2].split(","));
            bottles.add(new Bottle(layers, bottleCapacity));
        }
        return bottles;
    }

    // Function to apply an action and return the new state
    private String applyAction(String state, String action) {
        String[] parts = state.split(";");
        int numBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);
        List<Bottle> bottles = parseBottles(parts, numBottles, bottleCapacity);

        String[] actionParts = action.split("_");
        int from = Integer.parseInt(actionParts[1]);
        int to = Integer.parseInt(actionParts[2]);

        bottles.get(from).pourInto(bottles.get(to));

        // Convert the list of bottles back to state string
        return numBottles + ";" + bottleCapacity + ";" + bottlesToStateString(bottles);
    }

    // Function to convert a list of bottles back to a state string
    private String bottlesToStateString(List<Bottle> bottles) {
        StringBuilder state = new StringBuilder();
        for (Bottle bottle : bottles) {
            state.append(bottle.toString()).append(";");
        }
        return state.toString();
    }

    // Check if state is already in the queue (for BFS)
    private boolean stateInQueue(Queue<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Check if state is already in the stack (for DFS)
    private boolean stateInStack(Stack<Node> frontier, String state) {
        for (Node node : frontier) {
            if (node.state.equals(state)) {
                return true;
            }
        }
        return false;
    }

    // Construct the solution from the final node
    private String constructSolution(Node node, int nodesExpanded) {
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
}
