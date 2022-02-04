package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.ConstantOperations.Nop;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class EmptyRule<T extends Node> extends SubstitutionRule<T> {
    
    @Override
    public int getCost() {
        return 0;
    }
    
    @Override
    public List<Operation> substitute() {
        return List.of(new Nop());
    }
    
    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return new ArrayList<>();
    }

    @Override
    public boolean matches(T inputNode) {
        return true;
    }
}
