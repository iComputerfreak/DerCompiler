package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Shr;

import java.util.List;

public class ShrRule extends SubstitutionRule<Shr> {
    
    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shr getShr() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getShr().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getShr().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation shr = new BinaryOperation(BinaryOperationType.SHR, getLeft().getTarget(), getRight().getTarget());
        shr.setMode(getMode());
        return List.of(shr);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Shr inputNode) {
        // Any Shr node matches
        return inputNode != null;
    }
}
