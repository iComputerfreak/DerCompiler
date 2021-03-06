package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Node;

import java.util.List;

public class AddRule extends SubstitutionRule<Add> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    Add getAdd() {
        return getRootNode();
    }

    NodeAnnotation<?> getLeft() {
        return getAnnotation(getAdd().getLeft());
    }

    NodeAnnotation<?> getRight() {
        return getAnnotation(getAdd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation add = new de.dercompiler.intermediate.operation.BinaryOperations.Add(getLeft().getDefinition(), getRight().getDefinition());
        add.setMode(getRootNode().getMode());
        setMode(getRootNode().getMode());
        autosetDefinitions(add);
        return List.of(add);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }


    @Override
    public boolean matches(Add add) {
        // any Add node matches
        return !add.getMode().equals(firm.Mode.getP());
    }

}
