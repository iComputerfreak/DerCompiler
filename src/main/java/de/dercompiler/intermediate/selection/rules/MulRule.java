package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Mul;
import firm.nodes.Node;
import firm.nodes.Sub;

import java.util.List;

public class MulRule extends SubstitutionRule {

    public MulRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Mul getMul() {
        if (node.getRootNode() instanceof Mul mul) {
            return mul;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("MulRule has no Mul root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getMul().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getMul().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation mul = new BinaryOperation(BinaryOperationType.MUL, getLeft().getTarget(), getRight().getTarget());
        mul.setMode(getRootNode().getMode());
        return List.of(mul);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Mul node matches
        return true;
    }
}
