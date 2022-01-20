package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Shrs;

import java.util.List;

public class ShrsRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shrs getShrs() {
        if (node.getRootNode() instanceof Shrs shrs) {
            return shrs;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("ShrsRule has no Shrs root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getShrs().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getShrs().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation shrs = new BinaryOperation(BinaryOperationType.SHRS, getLeft().getTarget(), getRight().getTarget());
        return List.of(shrs);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Shrs node matches
        return inputNode instanceof Shrs;
    }
}
