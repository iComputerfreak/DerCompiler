package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.And;
import firm.nodes.Node;

import java.util.List;

public class AndRule extends SubstitutionRule<And> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private And getAnd() {
        return getRootNode();
    }
    
    private NodeAnnotation<?> getLeft() {
        return getAnnotation(getAnd().getLeft());
    }

    private NodeAnnotation<?> getRight() {
        return getAnnotation(getAnd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation and = new BinaryOperation(BinaryOperationType.AND, getLeft().getTarget(), getRight().getTarget());
        and.setMode(getRootNode().getMode());
        return List.of(and);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(And inputNode) {
        // Any And node matches
        return inputNode != null;
    }

}
