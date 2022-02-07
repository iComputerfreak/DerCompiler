package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.MethodReference;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.MethodType;
import firm.nodes.Address;
import firm.nodes.Call;
import firm.nodes.Node;

import java.util.List;
import java.util.stream.IntStream;

import static de.dercompiler.intermediate.selection.IRMode.NODATA;

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
        List<IRMode> argsModes = IntStream.range(2, getCall().getPredCount())
                .mapToObj(getCall()::getPred)
                .map(Node::getMode)
                .map(IRMode::forMode).toList();
        de.dercompiler.intermediate.operation.NaryOperations.Call call = new de.dercompiler.intermediate.operation.NaryOperations.Call(
                getMethod(),
                true,
                argsModes,
                getArgRegister());
        call.setMode(getResultMode());
        if (call.getMode().equals(NODATA)) {
            call.setDefinition(null);
            setDefinition(null);
        } else {
            autosetDefinitions(call);
        }

        return List.of(call);
    }

    private IRMode getResultMode() {
        if (getMethodType().getNRess() > 0) {
            return IRMode.forMode(getMethodType().getResType(0).getMode());
        }
        return NODATA;
    }

    private MethodType getMethodType() {
        Address ptr = (Address) node.getPtr();
        MethodType type = (MethodType) ptr.getEntity().getType();
        return type;
    }

    private Operand[] getArgRegister() {
        return IntStream.range(2, getCall().getPredCount()).mapToObj(idx -> getAnnotation(getCall().getPred(idx)).getDefinition()).toArray(Operand[]::new);
    }

    private MethodReference getMethod() {
        Address methodNode = (Address) getCall().getPtr();
        return new MethodReference(methodNode.getEntity());
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
