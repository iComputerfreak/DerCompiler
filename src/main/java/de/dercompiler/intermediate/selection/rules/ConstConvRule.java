package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;

import firm.nodes.Const;
import firm.nodes.Conv;
import firm.nodes.Node;

import java.util.List;

public class ConstConvRule extends SubstitutionRule {

    public ConstConvRule(Node rootNode) {
        super(rootNode);
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public List<Operation> substitute() {
        // save as constant operand, no operations
        annotationSupplier.apply(getRootNode()).setTarget(new Address(getConst().getTarval().asInt(), null));
        return List.of();
    }

    private Conv getConv() {
        if (getRootNode() instanceof Conv conv) return conv;
        throw new RuntimeException();
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
    public boolean matches(Node inputNode) {
        return inputNode instanceof Conv conv
                && conv.getOp() instanceof Const constant;
    }
}
