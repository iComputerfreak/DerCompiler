package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.End;
import firm.nodes.Node;
import firm.nodes.Start;

import java.util.List;

public class EndRule extends SubstitutionRule<End> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        this.setTarget(null);
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(End inputNode) {
        return inputNode != null;
    }
}
