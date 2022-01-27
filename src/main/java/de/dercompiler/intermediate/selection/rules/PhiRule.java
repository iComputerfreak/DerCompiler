package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.ConstantOperations.Nop;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Phi;

import java.util.List;
import java.util.Objects;

public class PhiRule extends SubstitutionRule<Phi> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        Phi root = getRootNode();
        if (Objects.equals(root.getMode(), Mode.getM())) {
            // not represented in memory
            this.setTarget(null);
        }

        /** The code for the different Phi blocks is supposed to be created in getCodeForPred(int) */
        setMode(root.getPred(0).getMode());
        setTarget(new VirtualRegister());
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

    public int getPredCount() {
        return getRootNode().getPredCount();
    }

    public Operation getCodeForSucc(int i) {
        Phi root = getRootNode();
        Operand target = getAnnotation(root).getTarget();
        Operand source = getAnnotation(root.getPred(i)).getTarget();
        Mov mov = new Mov(target, source, false);
        mov.setMode(getAnnotation(root.getPred(i)).getRootNode().getMode());
        return mov;
    }
}
