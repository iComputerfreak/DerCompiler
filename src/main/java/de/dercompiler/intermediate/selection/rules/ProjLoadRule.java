package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Load;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class ProjLoadRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1 + getAnnotation(getLoad()).getCost();
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
        Operation mov = new Mov(getAnnotation(getLoad()).getTarget(), target, isMemoryOperation());
        mov.setMode(getRootNode().getMode());
        return List.of(mov);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj proj) {
        return proj != null
                && !Objects.equals(proj.getMode(), Mode.getM())
                && proj.getPred() instanceof Load;
    }
}
