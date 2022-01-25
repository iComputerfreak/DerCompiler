package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.BinaryOperations.Xor;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Const;
import firm.nodes.Node;
import firm.nodes.Store;

import java.util.List;

public class ResetRule extends SubstitutionRule<Store> {
    @Override
    public int getCost() {
        return 1 + getAnnotation(getTarget()).getCost();
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
        Operation eor = new Xor(
                targetReg,
                targetReg);
        this.setTarget(targetReg);
        setMode(getValue().getMode());
        return List.of(eor);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Store inputNode) {
        return inputNode != null && inputNode.getValue() instanceof Const c && c.getTarval().asInt() == 0;
    }
}
