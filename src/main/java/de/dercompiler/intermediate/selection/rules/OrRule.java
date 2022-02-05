package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Or;

import java.util.List;

public class OrRule extends SubstitutionRule<Or> {
    
    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Or getOr() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getOr().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getOr().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation or = new de.dercompiler.intermediate.operation.BinaryOperations.Or(getLeft().getDefinition(), getRight().getDefinition(), isMemoryOperation());
        or.setMode(getLeft().getRootNode().getMode());
        autosetDefinitions(or);
        return List.of(or);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Or inputNode) {
        return inputNode != null;
    }
}
