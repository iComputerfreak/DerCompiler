package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperations.Add;
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
        Operation add = new Add(getLeft().getDefinition(), getRight().getDefinition());
        add.setMode(getRootNode().getMode());
        setDefinition(add.getDefinition());
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
