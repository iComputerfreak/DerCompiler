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
import firm.nodes.Shr;

import java.util.List;

public class ShrRule extends SubstitutionRule {
    
    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shr getShr() {
        if (node.getRootNode() instanceof Shr shr) {
            return shr;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("ShrRule has no Shr root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getLeft() {
        return annotationSupplier.apply(getShr().getLeft());
    }

    private NodeAnnotation getRight() {
        return annotationSupplier.apply(getShr().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation shr = new BinaryOperation(BinaryOperationType.SHR, getLeft().getTarget(), getRight().getTarget());
        return List.of(shr);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getLeft().getRootNode(), getRight().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        // Any Shr node matches
        return inputNode instanceof Shr;
    }
}
