package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.operation.UnaryOperationType;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Minus;
import firm.nodes.Node;

import java.util.List;

public class MinusRule extends SubstitutionRule<Minus> {

    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private Minus getMinus() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getOperator() {
        return getTypedAnnotation(getMinus().getOp());
    }

    @Override
    public List<Operation> substitute() {
        Operation minus = new UnaryOperation(UnaryOperationType.NEG, getOperator().getTarget());
        minus.setMode(getMode());
        VirtualRegister target = new VirtualRegister();
        target.setMode(getMode());
        node.setTarget(target);
        return List.of(minus);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Minus inputNode) {
        // Any Minus node matches
        return inputNode != null;
    }
}
