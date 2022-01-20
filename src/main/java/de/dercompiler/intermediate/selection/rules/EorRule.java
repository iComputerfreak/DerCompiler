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
import firm.nodes.Eor;
import firm.nodes.Node;

import java.util.List;

public class EorRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Eor getSub() {
        if (node.getRootNode() instanceof Eor eor) {
            return eor;
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
        Operation eor = new BinaryOperation(BinaryOperationType.XOR, getLeft().getTarget(), getRight().getTarget());
        eor.setMode(Mode.getBu());
        return List.of(eor);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Eor node matches
        return inputNode instanceof Eor;
    }
}
