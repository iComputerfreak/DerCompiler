package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.And;
import firm.nodes.Node;

import java.util.List;

public class AndRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private And getAnd() {
        if (node.getRootNode() instanceof And and) {
            return and;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("AndRule has no And root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getAnd().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getAnd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation and = new BinaryOperation(BinaryOperationType.AND, getLeft().getTarget(), getRight().getTarget());
        and.setMode(Mode.getBu());
        return List.of(and);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any And node matches
        return inputNode instanceof And;
    }
}
