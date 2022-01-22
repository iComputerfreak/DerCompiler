package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Load;
import firm.nodes.Node;

import java.util.List;

public class LoadRule extends SubstitutionRule<Load> {
    @Override
    public int getCost() {
        return 1;  //+ getAnnotation(getLoad()).getCost();
    }


    private Load getLoad() {
      return getRootNode();
    }

    private NodeAnnotation<?> getOperand() {
        return getAnnotation(getRootNode().getPtr());
    }

    @Override
    public List<Operation> substitute() {
        setTarget(getOperand().getTarget());
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Load proj) {
        return proj != null;
    }
}
