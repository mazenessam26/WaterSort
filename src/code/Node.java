    package code;

    import java.util.*;

    public class Node {

        private Object state;         // The current state (e.g., arrangement of liquids in bottles)
        private Node parent;          // The parent node (the node from which this node was generated)
        private String action;        // The action that led to this node (e.g., "pour_0_1")
        private double pathCost;      // The cumulative cost to reach this node from the root node
        private int depth;            // The depth of this node in the search tree

        /**
         * Constructor for creating a new node.
         * @param state The current state of the puzzle.
         * @param parent The parent node (null if this is the root node).
         * @param action The action that led to this node.
         * @param pathCost The cumulative cost to reach this node.
         * @param depth The depth of this node in the search tree.
         */
        public Node(Object state, Node parent, String action, double pathCost, int depth) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.pathCost = pathCost;
            this.depth = depth;
        }

        /**
         * Returns the current state of the node.
         * @return The current state.
         */
        public Object getState() {
            return state;
        }

        /**
         * Returns the parent node.
         * @return The parent node.
         */
        public Node getParent() {
            return parent;
        }

        /**
         * Returns the action that led to this node.
         * @return The action.
         */
        public String getAction() {
            return action;
        }

        /**
         * Returns the path cost to reach this node.
         * @return The path cost.
         */
        public double getPathCost() {
            return pathCost;
        }

        /**
         * Returns the depth of this node in the search tree.
         * @return The depth.
         */
        public int getDepth() {
            return depth;
        }

        /**
         * Returns the sequence of actions from the root to the current node.
         * This is used to trace back the solution path by traversing from the current node to the root.
         * @return A list of actions representing the solution path.
         */
        public List<String> getPath() {
            List<String> path = new ArrayList<>();
            Node currentNode = this;
            while (currentNode.getParent() != null) {
                path.add(0, currentNode.getAction()); // Add actions in reverse order, from root to current
                currentNode = currentNode.getParent();
            }
            return path;
        }

        /**
         * Returns the full path cost from the root to this node (for A* search).
         * This is the cost function for uniform cost search and A* search.
         * @return The full path cost.
         */
        public double getFullPathCost() {
            return pathCost + (parent != null ? parent.getPathCost() : 0);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "state=" + state +
                    ", action='" + action + '\'' +
                    ", pathCost=" + pathCost +
                    ", depth=" + depth +
                    '}';
        }
    }
