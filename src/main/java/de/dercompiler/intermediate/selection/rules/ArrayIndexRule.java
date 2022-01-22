package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;
import firm.nodes.Mul;
import firm.nodes.Node;

import java.util.List;

public class ArrayIndexRule extends MulRule {

    @Override
    public int getCost() {
        return getRight().getCost();
    }

    @Override
    public List<Operation> substitute() {
        Operation add = new BinaryOperation(BinaryOperationType.ADD, getLeft().getTarget(), getRight().getTarget());
        add.setMode(getRootNode().getMode());
        return List.of(add);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Mul inputNode) {
        return super.matches(inputNode);
                //&& annotationSupplier.apply(getLeft().getRootNode()).getTarget().

    }
}
