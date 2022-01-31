package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.MethodReference;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.Signedness;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.MethodType;
import firm.nodes.Address;
import firm.nodes.Call;
import firm.nodes.Node;

import java.util.List;
import java.util.stream.IntStream;

public class CallRule extends SubstitutionRule<Call> {
    @Override
    public int getCost() {
        int cost = 1;
        for (int idx = 2; idx < getCall().getPredCount(); idx++) {
            Node n = getCall().getPred(idx);
            cost += getAnnotation(n).getCost();
        }
        return cost;
    }

    @Override
    public List<Operation> substitute() {
        de.dercompiler.intermediate.operation.NaryOperations.Call call = new de.dercompiler.intermediate.operation.NaryOperations.Call(
                getMethod(),
                true,
                getArgRegister());
        Operand target = getAnnotation(node).getTarget();
        if (target != null) {
            call.setDefinition(target);
        } else {
            setTarget(call.getDefinition());
        }
        call.setMode(getResultMode());
        return List.of(call);
    }

    private IRMode getResultMode() {
        if (getMethodType().getNRess() > 0) {
            return IRMode.forMode(getMethodType().getResType(0).getMode());
        }
        return new IRMode(Datatype.NODATA, Signedness.UNSIGNED);
    }

    private MethodType getMethodType() {
        Address ptr = (Address) node.getPtr();
        MethodType type = (MethodType) ptr.getEntity().getType();
        return type;
    }

    private Operand[] getArgRegister() {
        return IntStream.range(2, getCall().getPredCount()).mapToObj(idx -> getAnnotation(getCall().getPred(idx)).getTarget()).toArray(Operand[]::new);
    }

    private MethodReference getMethod() {
        Address methodNode = (Address) getCall().getPtr();
        return new MethodReference(methodNode.getEntity().getName());
    }

    private Call getCall() {
        return getRootNode();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Call inputNode) {
        return inputNode != null
                && inputNode.getPtr() instanceof Address method;

    }
}
