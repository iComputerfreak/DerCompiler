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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *  As a result of the ArithmeticOptimization, array accesses with element sizes of powers of 2 (2, 4, 8)
 *  are presented as (Add P (Shl Ls (Const 0x1/2/3) (Idx)) Base). This rule reverses this effect.
 */
public class ArrayAccessShlLRule extends AddRule {

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
     * The Shl node of the ArrayAccess subtree that contains the (index * scale) portion of the array access.
     *
     * @return the Shl child node
     */
    private Shl getOffset() {
        NodeAnnotation<?> op2 = getRight();
        if (op2.getRootNode() instanceof Shl offset) {
            return offset;
        }
        // we never return
        throw new RuntimeException();
    }

    private int getScale() {
        Shl offset = getOffset();
        Operand target = getTypedAnnotation(offset.getLeft()).getTarget();
        if (target instanceof ConstantValue scale) {
            return 2 << scale.getValue();
        }
        // we never return
        throw new RuntimeException();
    }


    @Override
    public List<Operation> substitute() {
        Operand index = getIndex().getTarget();
        Address target;
        List<Operation> ops = new LinkedList<>();

        if (getArray() == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getLeft().getRootNode().toString()));
        }
        Address address = Address.ofOperand(getArray());

        if (index instanceof Register idxReg) {
            // index is already a register, so no operation needed
            target = address.setIndex(idxReg, getScale());
        } else {
            // index needs to be moved to a register so that we can use it as the index register
            VirtualRegister idxReg = new VirtualRegister();
            target = address.setIndex(idxReg, getScale());
            getIndex().setTarget(target);
            Lea lea = new Lea(idxReg, index);
            ops.add(lea);
        }

        VirtualRegister targetAddr = new VirtualRegister();
        ops.add(new Lea(target, targetAddr));
        setTarget(targetAddr);
        return ops;
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        List<Node> nodes = List.of(
                getOffset(), // Shl
                getOffset().getLeft() // Const
        );

        return nodes;
    }

    @Override
    public boolean matches(Add add) {
        return add != null
                && Objects.equals(add.getMode(), Mode.getP())
                && add.getLeft() instanceof Proj array
                && add.getRight() instanceof Shl offset
                && ((offset.getLeft() instanceof Const constant) || (offset.getLeft() instanceof Conv conv && conv.getOp() instanceof Const));
    }
}
