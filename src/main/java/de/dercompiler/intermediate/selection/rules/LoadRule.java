package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Load;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;

public class LoadRule extends SubstitutionRule {
    @Override
    public int getCost() {
        return 1 + getTypedAnnotation(getLoad()).getCost();
    }

    private Node getLoad() {
        if (getProj().getPred() instanceof Load load) {
            return load;
        };
        // we never return
        throw new RuntimeException();
    }

    private Proj getProj() {
        if (getRootNode() instanceof Proj proj) {
            return proj;
        }
        // we never return
        throw new RuntimeException();
    }


    @Override
    public List<Operation> substitute() {
        VirtualRegister target = new VirtualRegister();
        target.setMode(getMode());
        getAnnotation(getRootNode()).setTarget(target);
        BinaryOperation mov = new BinaryOperation(BinaryOperationType.MOV, getAnnotation(getLoad()).getTarget(), target);
        mov.setMode(getMode());
        return List.of(mov);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Node inputNode) {
        return inputNode instanceof Proj proj
                && proj.getPred() instanceof Load;
    }
}
