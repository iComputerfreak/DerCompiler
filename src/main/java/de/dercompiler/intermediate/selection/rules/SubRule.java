package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Sub;

import java.util.List;

public class SubRule extends SubstitutionRule<Sub> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Sub getSub() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getSub().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getSub().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation sub = new de.dercompiler.intermediate.operation.BinaryOperations.Sub(getLeft().getDefinition(), getRight().getDefinition());
        sub.setMode(getRootNode().getMode());
        autosetDefinitions(sub);
        return List.of(sub);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Sub inputNode) {
        return inputNode != null;
    }
}
