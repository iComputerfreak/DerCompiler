package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.operation.UnaryOperationType;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Minus;
import firm.nodes.Node;

import java.util.List;

public class MinusRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private Minus getMinus() {
        if (node.getRootNode() instanceof Minus minus) {
            return minus;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("MinusRule has no Minus root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getOperator() {
        return annotationSupplier.apply(getMinus().getOp());
    }

    @Override
    public List<Operation> substitute() {
        Operation minus = new UnaryOperation(UnaryOperationType.NEG, getOperator().getTarget());
        minus.setMode(getRootNode().getMode());
        return List.of(minus);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getOperator().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Minus node matches
        return inputNode instanceof Minus;
    }
}
