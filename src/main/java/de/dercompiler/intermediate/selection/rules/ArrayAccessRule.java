package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.Lea;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ArrayAccessRule extends AddRule {

    @Override
    public int getCost() {
        return 1 + (getIndex() instanceof Register ? 0 : getIndex().getCost());
    }


    private Operand getArray() {
        return getLeft().getTarget();
    }

    private NodeAnnotation<Node> getIndex() {
        return getTypedAnnotation(getOffset().getRight());
    }

    /**
     * The Mul node of the ArrayAccess subtree that contains the (index * scale) portion of the array access.
     *
     * @return the Mul child node
     */
    private Mul getOffset() {
        NodeAnnotation<?> op2 = getRight();
        if (op2.getRootNode() instanceof Mul offset) {
            return offset;
        }
        // we never return
        throw new RuntimeException();
    }

    private int getScale() {
        Mul offset = getOffset();
        Operand target = getTypedAnnotation(offset.getLeft()).getTarget();
        if (target instanceof ConstantValue scale) {
            return scale.getValue();
        }
        // we never return
        throw new RuntimeException();
    }


    @Override
    public List<Operation> substitute() {
        Operand index = getIndex().getTarget();
        Address target;
        // Load eff. address into vReg
        VirtualRegister targetAddr = new VirtualRegister();

        List<Operation> ops;

        if (getArray() == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getLeft().getRootNode().toString()));
        }
        Address address = Address.ofOperand(getArray());

        ops = new LinkedList<>();
        if (index instanceof Register idxReg) {
            // index is already a register, so no operation needed
            target = address.setIndex(idxReg, getScale());
        } else if (index instanceof Address tAddr && tAddr.isRegister()) {
            // index is already a register, so no operation needed
            target = address.setIndex(tAddr.asRegister(), getScale());
        } else {
            // Load effective address of index
            VirtualRegister idxReg = new VirtualRegister();
            target = address.setIndex(idxReg, getScale());
            getIndex().setTarget(target);
            ops.add(new Lea(idxReg, index));
        }

        ops.add(new Lea(targetAddr, target));

        setTarget(target);
        return ops;
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(getOffset()); // Const
        nodes.add(getOffset().getLeft()); // Mul
        if (getOffset().getRight() instanceof Conv conv) nodes.add(conv);

        return nodes;
    }

    @Override
    public boolean matches(Add add) {
        return add != null
                && Objects.equals(add.getMode(), Mode.getP())
                && add.getLeft() instanceof Proj array
                && add.getRight() instanceof Mul offset
                && (offset.getLeft() instanceof Conv conv || getOffset().getLeft() instanceof Const constant);
    }
}
