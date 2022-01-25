package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Shl;

import java.util.List;

public class ShlRule extends SubstitutionRule<Shl> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shl getShl() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getShl().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getShl().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation shl = new de.dercompiler.intermediate.operation.BinaryOperations.Shl(getLeft().getTarget(), getRight().getTarget());
        return List.of(shl);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Shl inputNode) {
        // Any Shl node matches
        return inputNode != null;
    }
}
