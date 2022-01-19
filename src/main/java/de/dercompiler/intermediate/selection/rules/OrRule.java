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
import firm.nodes.Or;
import firm.nodes.Node;

import java.util.List;

public class OrRule extends SubstitutionRule {
    
    public OrRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Or getOr() {
        if (node.getRootNode() instanceof Or or) {
            return or;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("OrRule has no Or root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getOr().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getOr().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation or = new BinaryOperation(BinaryOperationType.OR, getLeft().getTarget(), getRight().getTarget());
        or.setMode(Mode.getBu());
        return List.of(or);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Or node matches
        return true;
    }
}
