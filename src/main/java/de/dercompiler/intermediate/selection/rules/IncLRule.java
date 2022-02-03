package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Inc;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Const;
import firm.nodes.Node;

import java.util.List;

public class IncLRule extends AddRule {

    @Override
    public int getCost() {
        return 1 + getOperator().getCost();
    }

    private NodeAnnotation<?> getOperator() {
        return getAnnotation(getAdd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operand target = getOperator().getDefinition();
        setTarget(target);
        setMode(getOperator().getRootNode().getMode());

        Operation inc = new Inc(target, isMemoryOperation());
        inc.setDefinition(target);
        inc.setMode(node.getMode());
        return List.of(inc);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getAdd().getLeft());
    }

    @Override
    public boolean matches(Add add) {
        return add != null
                && add.getLeft() instanceof Const constant
                && constant.getTarval().asInt() == 1;
    }
}
