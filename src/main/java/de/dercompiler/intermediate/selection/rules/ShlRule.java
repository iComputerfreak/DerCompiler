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
import firm.nodes.Shl;

import java.util.List;

public class ShlRule extends SubstitutionRule {

    public ShlRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shl getShl() {
        if (node.getRootNode() instanceof Shl shl) {
            return shl;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("AddRule has no Add root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getShl().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getShl().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation shl = new BinaryOperation(BinaryOperationType.SHL, getLeft().getTarget(), getRight().getTarget());
        return List.of(shl);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Shl node matches
        return true;
    }
}
