package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class ProjRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public List<Operation> substitute() {
        if (Objects.equals(getMode(), Mode.getM())) {
            // not represented in memory
            this.setTarget(null);
        }
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getRootNode());
    }

    @Override
    public boolean matches(Proj inputNode) {
        return inputNode != null;
    }
}
