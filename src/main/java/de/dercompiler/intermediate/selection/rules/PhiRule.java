package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Phi;

import java.util.List;
import java.util.Objects;

public class PhiRule extends SubstitutionRule<Phi> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        if (Objects.equals(getRootNode().getMode(), Mode.getM())) {
            // not represented in memory
            this.setTarget(null);
        }
        //TODO: Encode phi operation inside of 'special phi block'
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Phi inputNode) {
        return inputNode != null;
    }
}
