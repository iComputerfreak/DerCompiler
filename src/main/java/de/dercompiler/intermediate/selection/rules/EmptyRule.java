package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.ConstantOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class EmptyRule extends SubstitutionRule {
    
    public EmptyRule(Node rootNode) {
        super(rootNode);
    }
    
    @Override
    public int getCost() {
        return 0;
    }
    
    @Override
    public List<Operation> substitute(Node node) {
        return List.of(new ConstantOperation(ConstantOperationType.NOP));
    }
    
    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return new ArrayList<>();
    }
}