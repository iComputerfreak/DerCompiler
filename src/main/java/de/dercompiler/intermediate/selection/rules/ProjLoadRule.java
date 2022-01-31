package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Load;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class ProjLoadRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1 + getAnnotation(getLoad().getPred(1)).getCost();
    }

    private Node getLoad() {
        if (getProj().getPred() instanceof Load load) {
            return load;
        }

        // we never return
        throw new RuntimeException();
    }

    private Proj getProj() {
        return getRootNode();
    }


    @Override
    public List<Operation> substitute() {
        // Loaded value must be stored away!
        Operand operandTarget = getAnnotation(getLoad()).getTarget();
        if (operandTarget instanceof Address addr && addr.isRegister()) {
            setTarget(addr);
            return List.of();
        } else {
            Operand target = getAnnotation(node).getTarget();
            if (target == null) {
                target = new VirtualRegister();
                setTarget(target);
            }
            Operation mov = new Mov(target, operandTarget, true );
            mov.setMode(getRootNode().getMode());
            return List.of(mov);
        }
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Proj proj) {
        return proj != null
                && !Objects.equals(proj.getMode(), Mode.getM())
                && proj.getPred() instanceof Load;
    }
}
