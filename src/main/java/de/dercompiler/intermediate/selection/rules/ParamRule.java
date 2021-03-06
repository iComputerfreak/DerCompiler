package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Proj;
import firm.nodes.Start;

import java.util.List;
import java.util.Objects;

public class ParamRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        ParameterRegister target = new ParameterRegister(getRootNode().getNum());
        setDefinition(target);

        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj inputNode) {
        return inputNode != null
                && inputNode.getPred() instanceof Proj pred
                && Objects.equals(pred.getMode(), Mode.getT())
                && pred.getPred() instanceof Start;
    }
}
