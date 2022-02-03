package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Not;

import java.util.List;

public class NotRule extends SubstitutionRule<Not> {
    
    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private Not getNot() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getOperator() {
        return getTypedAnnotation(getNot().getOp());
    }

    @Override
    public List<Operation> substitute() {
        Operation not = new de.dercompiler.intermediate.operation.UnaryOperations.Not(getOperator().getDefinition(), isMemoryOperation());
        not.setMode(getOperator().getRootNode().getMode());
        setTarget(not.getDefinition());
        return List.of(not);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Not inputNode) {
        // Any Not node matches
        return inputNode != null;
    }
}
