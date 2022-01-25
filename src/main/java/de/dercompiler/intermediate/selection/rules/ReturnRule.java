package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Node;
import firm.nodes.Return;

import java.util.List;
import java.util.Objects;

public class ReturnRule extends SubstitutionRule<Return> {
    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public List<Operation> substitute() {
        Ret ret;
        if (Objects.isNull(getRetVal())) {
            ret = new Ret();
        } else {
            ret = new Ret(getRetVal().getTarget());
            ret.setMode(getRetVal().getRootNode().getMode());
        }
        this.setTarget(null);
        return List.of(ret);
    }

    private NodeAnnotation<?> getRetVal() {
        if (getReturn().getPredCount() > 1) return getAnnotation(getReturn().getPred(1));
        return null;
    }

    private Return getReturn() {
        return getRootNode();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Return inputNode) {
        return inputNode != null;
    }
}
