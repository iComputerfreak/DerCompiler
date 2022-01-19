package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.operation.UnaryOperationType;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.Not;
import firm.nodes.Node;

import java.util.List;

public class NotRule extends SubstitutionRule {
    
    public NotRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private Not getNot() {
        if (node.getRootNode() instanceof Not not) {
            return not;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("NotRule has no Not root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getOperator() {
        return annotationSupplier.apply(getNot().getOp());
    }

    @Override
    public List<Operation> substitute() {
        Operation not = new UnaryOperation(UnaryOperationType.NOT, getOperator().getTarget());
        not.setMode(Mode.getBu());
        return List.of(not);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getOperator().getRootNode());
    }
}
