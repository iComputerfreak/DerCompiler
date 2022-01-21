package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Store;

import java.util.List;

public class StoreRule extends SubstitutionRule {
    @Override
    public int getCost() {
        return 1;
    }

    private Node getTarget() {
        return getStore().getPtr();
    }

    private Node getValue() {
        return getStore().getValue();
    }

    private Store getStore() {
        if (getRootNode() instanceof Store store) {
            return store;
        }
        // we never return
        throw new RuntimeException();
    }

    @Override
    public List<Operation> substitute() {
        VirtualRegister target = new VirtualRegister();
        target.setMode(getMode());
        node.setTarget(target);

        Operation mov = new BinaryOperation(BinaryOperationType.MOV,
                getAnnotation(getTarget()).getTarget(),
                getAnnotation(getValue()).getTarget());
        mov.setMode(getMode());
        return List.of(mov);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Node inputNode) {
        return false;
    }
}
