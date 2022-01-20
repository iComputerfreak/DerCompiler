package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.operation.UnaryOperationType;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Const;
import firm.nodes.Node;

import java.util.List;

public class IncLRule extends SubstitutionRule {

    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private Add getAdd() {
        if (node.getRootNode() instanceof Add add) {
            return add;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                .internalError("IncLRule has no Add root node");
        // We never return
        throw new RuntimeException();
    }
    
    private NodeAnnotation getOperator() {
        return annotationSupplier.apply(getAdd().getLeft());
    }

    @Override
    public List<Operation> substitute() {
        Operation inc = new UnaryOperation(UnaryOperationType.INC, getOperator().getTarget());
        inc.setMode(getRootNode().getMode());
        return List.of(inc);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getOperator().getRootNode());
    }

    @Override
    public boolean matches(Node inputNode) {
        return inputNode instanceof Add add
                && add.getLeft() instanceof Const constant
                && constant.getTarval().asInt() == 1;
    }
}
