package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Node;

import java.util.List;

public class AddRule extends SubstitutionRule<Add> {

    @Override
    public int getCost() {
        return 2;
    }

    Add getAdd() {
        return getRootNode();
    }
    
    NodeAnnotation<?> getLeft() {
        return getAnnotation(getAdd().getLeft());
    }

    NodeAnnotation<?> getRight() {
        return getAnnotation(getAdd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation add = new BinaryOperation(BinaryOperationType.ADD, getLeft().getTarget(), getRight().getTarget());
        add.setMode(getRootNode().getMode());
        return List.of(add);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Add inputNode) {
        // any Add node matches
        return true;
    }

}