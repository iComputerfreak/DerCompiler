package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Jmp;
import firm.nodes.Node;

import java.util.List;

public class JmpRule extends SubstitutionRule<Jmp> {

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        //TODO the target block is actually the successor of the JMP node. How do we get it?
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Jmp inputNode) {
        return inputNode != null;
    }
}
