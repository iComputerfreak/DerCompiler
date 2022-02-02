package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Phi;

import java.util.List;

public class PhiRule extends SubstitutionRule<Phi> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        Phi root = getRootNode();

        // avoid intra-block cycles
        for (int i = 0; i < 2; i++) {
            if (root.getPred(i).equals(root)) {
                root.setPred(i, root.getPred((i + 1) % 2));
            }
        }

        if (root.getMode().equals(Mode.getM())) {
            // not represented in memory
            setTarget(null);
            return List.of();
        }

        /* The code for the different Phi blocks is supposed to be created in getCodeForPred(int) */
        setMode(root.getPred(0).getMode());
        Operand target = getAnnotation(node).getTarget();
        if (target == null) {
            target = new VirtualRegister();
            setTarget(target);
        }
        for (Node pred : node.getPreds()) {
            if (!(getAnnotation(pred).getTarget() instanceof ConstantValue)) {
                getAnnotation(pred).setTarget(target);
            }
        }

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

    /**
     * Creates a node that copies the modified value of the i-th successor back to the phi variable.
     *
     * @param i index of the successor
     * @return an operation to do the copying
     */
    public Operation getCodeForSucc(int i) {
        Phi root = getRootNode();
        Operand target = getAnnotation(root).getTarget();
        Operand source = getAnnotation(root.getPred(i)).getTarget();
        Mov mov = new Mov(target, source, isMemoryOperation());
        mov.setMode(getAnnotation(root.getPred(i)).getRootNode().getMode());
        return mov;
    }
}
