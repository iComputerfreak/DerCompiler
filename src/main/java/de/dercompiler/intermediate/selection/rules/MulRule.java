package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Mul;
import firm.nodes.Node;

import java.util.List;

public class MulRule extends SubstitutionRule<Mul> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Mul getMul() {
        return getRootNode();
    }
    
    NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getMul().getLeft());
    }

    NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getMul().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation mul = new de.dercompiler.intermediate.operation.BinaryOperations.Mul(getLeft().getTarget(), getRight().getTarget(), isMemoryOperation());
        mul.setMode(getRootNode().getMode());
        return List.of(mul);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Mul inputNode) {
        // Any Mul node matches
        return inputNode != null;
    }
}
