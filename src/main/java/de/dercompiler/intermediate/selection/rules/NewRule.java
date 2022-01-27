package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Address;
import firm.nodes.Call;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class NewRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1 + getConstructorCall().getCost() ;
    }

    @Override
    public List<Operation> substitute() {
        setTarget(getConstructorCall().getTarget());
        return List.of();
    }

    private NodeAnnotation<Call> getConstructorCall() {
        if (node.getPred() instanceof Proj tProj
            && tProj.getPred() instanceof Call call) {
            return getTypedAnnotation(call);
        }
        // we never return
        throw new RuntimeException();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj inputNode) {
        return inputNode != null
                && Objects.equals(inputNode.getMode(), Mode.getP())
                && inputNode.getPred() instanceof Proj tProj
                && tProj.getPred() instanceof Call call
                && call.getPtr() instanceof Address methodAddr
                && methodAddr.getEntity().getName().equals("allocate");
    }
}
