package code;


public class Node {
    public String state;   // The current state (bottles and their colors)
    public Node parent;    // Parent node in the search tree
    public String action;  // Action that led to this state (e.g., "pour_0_3")
    public int pathCost;   // The cost to reach this state

    public Node(String state, Node parent, String action, int pathCost) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.pathCost = pathCost;
    }
}
