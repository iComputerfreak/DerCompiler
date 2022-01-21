package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Const;
import firm.nodes.Node;

import java.util.List;

public class ConstRule extends SubstitutionRule<Const> {
    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public List<Operation> substitute() {
        Address target = new Address(getConst().getTarval().asInt(), null);
        target.setMode(getRootNode().getMode());
        node.setTarget(target);
        return List.of();
    }

    private Const getConst() {
        return getRootNode();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Const inputNode) {
        return inputNode != null;
    }
}
