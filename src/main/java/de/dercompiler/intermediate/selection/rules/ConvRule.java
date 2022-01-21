package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Conv;
import firm.nodes.Node;

import java.util.List;

public class ConvRule extends SubstitutionRule<Conv> {
    @Override
    public int getCost() {
        return 0;
    }

    public NodeAnnotation<?> getOperand() {
        return getAnnotation(getConv().getOp());
    }

    @Override
    public List<Operation> substitute() {

        Operand op = getOperand().getTarget();
        if (op == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getOperand().getRootNode().toString()));
        }

        Address target = Address.ofOperand(op);
        target.setMode(node.getRootNode().getMode());
        node.setTarget(target);

        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    private Conv getConv() {
        return getRootNode();
    }

    @Override
    public boolean matches(Conv inputNode) {
        return inputNode != null;
    }
}
