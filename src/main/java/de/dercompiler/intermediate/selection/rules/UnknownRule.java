package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Unknown;

import java.util.List;

/**
 *  This rule sets a dummy value for uninitialized values in cases of real "List und Tücke"
 */
public class UnknownRule extends SubstitutionRule<Unknown> {


    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public List<Operation> substitute() {
        setMode(node.getMode());
        setDefinition(Address.NULL_PTR);
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Unknown inputNode) {
        return true;
    }
}
