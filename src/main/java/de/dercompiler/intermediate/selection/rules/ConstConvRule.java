package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Const;
import firm.nodes.Conv;
import firm.nodes.Node;

import java.util.List;

public class ConstConvRule extends SubstitutionRule<Conv> {

    @Override
    public int getCost() {
        return 1 + getAnnotation(getConst()).getCost();
    }

    @Override
    public List<Operation> substitute() {
        // save as constant operand, no operations
        ConstantValue target = new ConstantValue(getConst().getTarval().asInt());
        setTarget(target);
        return List.of();
    }

    private Conv getConv() {
        return getRootNode();
    }

    private Const getConst() {
        if (getConv().getOp() instanceof Const c) return c;
        throw new RuntimeException();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getConst());
    }

    @Override
    public boolean matches(Conv inputNode) {
        return inputNode.getOp() instanceof Const;
    }

}
