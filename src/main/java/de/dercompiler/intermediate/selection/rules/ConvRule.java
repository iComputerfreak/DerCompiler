package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Cltq;
import de.dercompiler.intermediate.operation.UnaryOperations.Cwtl;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.Signedness;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Conv;
import firm.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class ConvRule extends SubstitutionRule<Conv> {
    @Override
    public int getCost() {
        return 1 + getAnnotation(getConv().getOp()).getCost();
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

        List<Operation> ops = new ArrayList<>();
        Datatype newType = Datatype.forMode(getRootNode().getMode());
        Datatype oldType = Datatype.forMode(getRootNode().getOp().getMode());
        while (oldType.compareTo(newType) < 0) {
            Operation convOp = null;
            switch (oldType) {
                case WORD -> {
                    convOp = new Cwtl(getOperand().getTarget(), isMemoryOperation());
                    convOp.setMode(oldType, getRootNode().getMode().isSigned() ? Signedness.SIGNED : Signedness.UNSIGNED);
                    oldType = Datatype.DWORD;
                }
                case DWORD -> {
                    convOp = new Cltq(getOperand().getTarget(), isMemoryOperation());
                    convOp.setMode(oldType, getRootNode().getMode().isSigned() ? Signedness.SIGNED : Signedness.UNSIGNED);
                    oldType = Datatype.QWORD;
                }
                default -> {
                    new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Unexpected conversion type");
                    throw new RuntimeException();
                }
            }
            convOp.setDefinition(getOperand().getTarget());
            ops.add(convOp);
        }

        Address target = Address.ofOperand(op);
        setTarget(target);

        return ops;
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
