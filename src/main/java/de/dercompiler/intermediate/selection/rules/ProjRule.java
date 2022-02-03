package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class ProjRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return Objects.equals(getRootNode().getMode(), Mode.getM()) ?
                0 :
                1 + getAnnotation(getOperand()).getCost();
    }

    private Node getOperand() {
        return getRootNode().getPred();
    }

    @Override
    public List<Operation> substitute() {
        switch (getRootNode().getMode().getName()) {
            // not represented in memory
            case "M", "X" -> this.setTarget(null);
            default -> {
                Operand predTarget = getAnnotation(getOperand()).getDefinition();
                //if (predTarget instanceof Address addr && addr.isRegister()) {
                //    setTarget(addr.asRegister());
                //}
                setTarget(predTarget);
            }
        }

        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj inputNode) {
        return inputNode != null;
    }
}
