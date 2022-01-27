package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Cmp;
import firm.nodes.Node;

import java.util.List;

public class CmpRule extends SubstitutionRule<Cmp> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private NodeAnnotation<?> getLeft() {
        return getAnnotation(getCmp().getLeft());
    }

    private NodeAnnotation<?> getRight() {
        return getAnnotation(getCmp().getRight());
    }

    private Cmp getCmp() {
        return getRootNode();
    }

    @Override
    public List<Operation> substitute() {
        Operation cmp = new de.dercompiler.intermediate.operation.BinaryOperations.Cmp(getLeft().getTarget(), getRight().getTarget());
        cmp.setMode(getCmp().getMode());

        // result of cmp is found in flag register
        getAnnotation(getRootNode()).setTarget(null);

        return List.of(cmp);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Cmp inputNode) {
        return inputNode != null;
    }
}
