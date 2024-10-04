package code;


import java.util.List;

public abstract class GenericSearch {

    public abstract boolean isGoalState(Object state);

    public abstract List<Object> getSuccessors(Object state);

    public abstract Object applyAction(Object state, String action);

    public abstract double getPathCost(List<String> path);

    public boolean goalTest(Object state) {
        return isGoalState(state);
    }
}
