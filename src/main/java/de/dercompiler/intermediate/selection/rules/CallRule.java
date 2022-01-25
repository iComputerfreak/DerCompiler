package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Address;
import firm.nodes.Call;
import firm.nodes.Node;

import java.awt.*;
import java.util.Arrays;
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
        return List.of(new de.dercompiler.intermediate.operation.NaryOperations.Call(
                getMethod(),
                IntStream.range(2, getCall().getPredCount()).mapToObj(idx -> getAnnotation(getCall().getPred(idx)).getTarget()).toArray(Operand[]::new)));
    }

    private LabelOperand getMethod() {
        Address methodNode = (Address) getCall().getPtr();
        return new LabelOperand(methodNode.getEntity().getName());
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
                && inputNode.getPtr() instanceof Address method
                && !method.getEntity().getName().equals("allocate");

    }
}
