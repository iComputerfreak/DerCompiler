package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Address;
import firm.nodes.Node;

import java.util.List;

public class AddressRule extends SubstitutionRule<Address> {

    @Override
    public int getCost() {
        return 1;
    }


    @Override
    public List<Operation> substitute() {
        setTarget(null);
        setMode(getRootNode().getMode());
        getAnnotation(node).setTransformed(true); // Address nodes are not useful, so we omit them
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Address inputNode) {
        return inputNode != null;
    }

}
