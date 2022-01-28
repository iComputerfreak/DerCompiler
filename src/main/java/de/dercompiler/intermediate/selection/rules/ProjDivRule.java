package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Div;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;

public class ProjDivRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1 + getDiv().getCost();
    }

    private NodeAnnotation<Div> getDiv() {
        Div pred = (Div) node.getPred();
        return getTypedAnnotation(pred);
    }

    @Override
    public List<Operation> substitute() {
        setTarget(getDiv().getTarget());
        setMode(getDiv().getRootNode().getMode());
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj inputNode) {
        return inputNode != null && inputNode.getPred() instanceof Div;
    }
}
