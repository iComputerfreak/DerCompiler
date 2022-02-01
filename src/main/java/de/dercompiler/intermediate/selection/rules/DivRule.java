package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.BinaryOperations.IDiv;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Div;
import firm.nodes.Node;

import java.util.List;

public class DivRule extends SubstitutionRule<Div> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    Div getDiv() {
        return getRootNode();
    }
    
    NodeAnnotation<?> getLeft() {
        return getAnnotation(getDiv().getLeft());
    }

    NodeAnnotation<?> getRight() {
        return getAnnotation(getDiv().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation div = new IDiv(getLeft().getTarget(), getRight().getTarget());
        div.setMode(getRootNode().getMode());
        setMode(getRootNode().getMode());
        setTarget(div.getDefinition());
        return List.of(div);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Div inputNode) {
        // any Div node matches
        return true;
    }

}
