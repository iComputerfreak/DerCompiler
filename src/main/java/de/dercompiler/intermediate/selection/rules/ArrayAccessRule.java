package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.*;

import java.util.List;
import java.util.Objects;

public class ArrayAccessRule extends AddRule {

    @Override
    public int getCost() {
        return 1;
        // + (getIndex() instanceof Register ? 0 : getIndex().getCost());
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
        if (target instanceof Address scale) {
            if (scale.isConstant()) {
                return scale.asConstant();
            }
        }
        // we never return
        throw new RuntimeException();
    }


    @Override
    public List<Operation> substitute() {
        Operand index = getIndex().getTarget();
        Address target;
        List<Operation> ops;

        if (getArray() == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getLeft().getRootNode().toString()));
        }
        Address address = Address.ofOperand(getArray());

        if (index instanceof Register idxReg) {
            // index is already a register, so no operation needed
            target = address.setIndex(idxReg, getScale());
            ops = List.of();
        } else if (index instanceof Address tAddr && tAddr.isRegister()) {
            // index is already a register, so no operation needed
            target = address.setIndex(tAddr.asRegister(), getScale());
            ops = List.of();
        } else {
            // index needs to be moved to a register so that we can use it as the index register
            VirtualRegister idxReg = new VirtualRegister();
            target = address.setIndex(idxReg, getScale());
            getIndex().setTarget(target);
            ops = List.of(new BinaryOperation(BinaryOperationType.MOV, idxReg, index));
        }

        getAnnotation(getRootNode()).setTarget(target);
        return ops;
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(
                getOffset(), // Mul
                getOffset().getLeft() // Const
        );
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
