package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Neg;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Minus;
import firm.nodes.Node;

import java.util.List;

public class MinusRule extends SubstitutionRule<Minus> {

    @Override
    public int getCost() {
        return 1 + getOperand().getCost();
    }

    private Minus getMinus() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getOperand() {
        return getTypedAnnotation(getMinus().getOp());
    }

    @Override
    public List<Operation> substitute() {
        // destination = operand
        Operation minus = new Neg(getOperand().getDefinition(), isMemoryOperation());
        minus.setMode(getOperand().getRootNode().getMode());
        if (!hasDefinition()) {
            if (!(getOperand().getDefinition() instanceof ParameterRegister)) {
                setDefinition(getOperand().getDefinition());
                minus.setDefinition(getDefinition());
            } else {
                setDefinition(minus.getDefinition());
            }
        } else {
            minus.setDefinition(getDefinition());
        }
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
