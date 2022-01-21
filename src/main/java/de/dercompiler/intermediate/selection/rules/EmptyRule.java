package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.ConstantOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class EmptyRule extends SubstitutionRule<Node> {
    
    @Override
    public int getCost() {
        return 1;
    }
    
    @Override
    public List<Operation> substitute() {
        return List.of(new ConstantOperation(ConstantOperationType.NOP));
    }
    
    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return new ArrayList<>();
    }

    @Override
    public boolean matches(Node inputNode) {
        return true;
    }
}
