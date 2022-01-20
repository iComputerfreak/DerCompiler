package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Node;

import java.util.List;

public class AddRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    Add getAdd() {
        if (node.getRootNode() instanceof Add add) {
            return add;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("AddRule has no Add root node");
        // We never return
        throw new RuntimeException();
    }
    
    NodeAnnotation getLeft() {
        return annotationSupplier.apply(getAdd().getLeft());
    }

    NodeAnnotation getRight() {
        return annotationSupplier.apply(getAdd().getRight());
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
    public boolean matches(Node inputNode) {
        // any Add node matches
        return inputNode instanceof Add;
    }
}
