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
import firm.nodes.Sub;

import java.util.List;

public class SubRule extends SubstitutionRule {

    public SubRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Sub getSub() {
        if (node.getRootNode() instanceof Sub sub) {
            return sub;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("AddRule has no Add root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getSub().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getSub().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation sub = new BinaryOperation(BinaryOperationType.SUB, getLeft().getTarget(), getRight().getTarget());
        sub.setMode(getRootNode().getMode());
        return List.of(sub);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Sub node matches
        return true;
    }
}
