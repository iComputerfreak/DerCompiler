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
    
    public AddRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Add getAdd() {
        if (node.getRootNode() instanceof Add add) {
            return add;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("AddRule has no Add root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getAdd().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getAdd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation add = new BinaryOperation(BinaryOperationType.ADD);
        // add.setOperands(getLeft().getTarget(), getRight().getTarget());
        return List.of(add);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return null;
    }
}
