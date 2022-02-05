package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperations.Sar;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Shrs;

import java.util.List;

public class SarRule extends SubstitutionRule<Shrs> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Shrs getShrs() {
        return getRootNode();
    }
    
    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getShrs().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getShrs().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation sar = new Sar(getLeft().getDefinition(), getRight().getDefinition());
        sar.setMode(getLeft().getRootNode().getMode());
        autosetDefinitions(sar);
        return List.of(sar);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Shrs inputNode) {
        // Any Shrs node matches
        return inputNode != null;
    }
}
