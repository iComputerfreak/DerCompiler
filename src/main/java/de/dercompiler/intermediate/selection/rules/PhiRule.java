package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Phi;
import firm.nodes.Sub;

import java.util.List;

public class PhiRule extends SubstitutionRule<Phi> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        VirtualRegister target = new VirtualRegister();
        target.setMode(getMode());
        node.setTarget(target);

        //TODO: Encode phi operation inside of 'special phi block'
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Phi inputNode) {
        return inputNode != null;
    }
}
