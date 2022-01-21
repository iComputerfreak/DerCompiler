package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Mul;
import firm.nodes.Node;

import java.util.List;

public class MulRule extends SubstitutionRule<Mul> {

    @Override
    public int getCost() {
        return 2;
    }

    private Mul getMul() {
        return getRootNode();
    }
    
    NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getMul().getLeft());
    }

    NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getMul().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation mul = new BinaryOperation(BinaryOperationType.MUL, getLeft().getTarget(), getRight().getTarget());
        mul.setMode(getMode());
        VirtualRegister target = new VirtualRegister();
        target.setMode(getMode());
        node.setTarget(target);
        return List.of(mul);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Mul inputNode) {
        // Any Mul node matches
        return inputNode != null;
    }
}
