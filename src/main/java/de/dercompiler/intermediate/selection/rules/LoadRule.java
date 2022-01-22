package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Load;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;

public class LoadRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1;  //+ getAnnotation(getLoad()).getCost();
    }

    private Node getLoad() {
        if (getProj().getPred() instanceof Load load) {
            return load;
        };
        // we never return
        throw new RuntimeException();
    }

    private Proj getProj() {
      return getRootNode();
    }


    @Override
    public List<Operation> substitute() {
        Operand target = getAnnotation(getRootNode()).getTarget();
        Operation mov = new BinaryOperation(BinaryOperationType.MOV, getAnnotation(getLoad()).getTarget(), target);
        mov.setMode(getMode());
        return List.of(mov);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj proj) {
        return proj != null
                && proj.getPred() instanceof Load;
    }
}
