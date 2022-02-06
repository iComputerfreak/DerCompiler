package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.Lea;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.ConstantOperations.Cqto;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Load;
import firm.nodes.Node;

import java.util.List;

public class LoadRule extends SubstitutionRule<Load> {
    @Override
    public int getCost() {
        return 1 + getOperand().getCost();
    }


    private Load getLoad() {
      return getRootNode();
    }

    private NodeAnnotation<?> getOperand() {
        return getAnnotation(getLoad().getPtr());
    }

    @Override
    public List<Operation> substitute() {
        setDefinition(getOperand().getDefinition());
        return List.of();

    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Load proj) {
        return proj != null;
    }
}
