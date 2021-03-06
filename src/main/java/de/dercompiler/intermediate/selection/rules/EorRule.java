package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperations.Xor;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Eor;
import firm.nodes.Node;

import java.util.List;

public class EorRule extends SubstitutionRule<Eor> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    private Eor getSub() {
        return getRootNode();
    }

    private NodeAnnotation<Node> getLeft() {
        return getTypedAnnotation(getSub().getLeft());
    }

    private NodeAnnotation<Node> getRight() {
        return getTypedAnnotation(getSub().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation eor = new Xor(getLeft().getDefinition(), getRight().getDefinition());
        eor.setMode(node.getMode());
        autosetDefinitions(eor);
        return List.of(eor);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Eor inputNode) {
        return inputNode != null;
    }

}
