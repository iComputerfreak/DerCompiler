package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Store;

import java.util.List;

public class StoreRule extends SubstitutionRule<Store> {
    @Override
    public int getCost() {
        return 1 + getAnnotation(getTarget()).getCost() + getAnnotation(getValue()).getCost();
    }

    private Node getTarget() {
        return getStore().getPtr();
    }

    private Node getValue() {
        return getStore().getValue();
    }

    private Store getStore() {
        return getRootNode();
    }

    @Override
    public List<Operation> substitute() {
        Operand targetReg = getAnnotation(getTarget()).getTarget();
        Operation mov = new Mov(
                targetReg,
                getAnnotation(getValue()).getTarget(), true
        );
        setTarget(targetReg);
        setMode(getValue().getMode());
        return List.of(mov);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Store inputNode) {
        return inputNode != null;
    }
}