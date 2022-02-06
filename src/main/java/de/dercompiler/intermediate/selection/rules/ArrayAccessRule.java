package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.Lea;
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


    private NodeAnnotation<Proj> getArray() {
        if (getLeft().getRootNode() instanceof Proj arrayNode)
        return getTypedAnnotation(arrayNode);
        throw new RuntimeException();
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
        Operand target = getTypedAnnotation(offset.getLeft()).getDefinition();
        if (target instanceof ConstantValue scale) {
            return scale.getValue();
        }
        // we never return
        throw new RuntimeException();
    }


    @Override
    public List<Operation> substitute() {
        Operand index = getIndex().getDefinition();
        Address target;

        // Load eff. address into vReg
        Operand targetAddr = getDefinition();

        List<Operation> ops;

        if (getArray().getDefinition() == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getLeft().getRootNode().toString()));
        }
        Address arrayAddr = Address.ofOperand(getArray().getDefinition());

        // There is nowhere to save the relative address, so calculate it again

        ops = new LinkedList<>();
        if (index instanceof Register idxReg) {
            // index is already a register, so no operation needed
            target = arrayAddr.setIndex(idxReg, getScale());
        } else if (index instanceof ConstantValue c && c.getValue() == 0) {
            // Yay! We access the zeroth member.
            target = arrayAddr;
        } else {
            // Load effective address of index

            // Hack: save index register in Mul node
            Operand indexAddr = getAnnotation(getOffset()).getDefinition();
            if (indexAddr == null) {
                indexAddr = getIndex().getDefinition();
                getAnnotation(getOffset()).setDefinition(indexAddr);
            }

            target = arrayAddr.setIndex(index, getScale());


        }

        if (targetAddr == null) {
            targetAddr = new VirtualRegister();
            setDefinition(Address.ofOperand(targetAddr));
        } else {
            // Undo indirection
            targetAddr = ((Address) targetAddr).getBase();
        }

        ops.add(new Lea(targetAddr, target));


        return ops;
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(getOffset()); // Const
        nodes.add(getOffset().getLeft()); // Mul

        return nodes;
    }

    @Override
    public List<NodeAnnotation<?>> getReplacementArgs() {
        // Mul subtree is replaced by index node.
        return List.of(getIndex());
    }

    @Override
    public boolean matches(Add add) {
        return add != null
                && Objects.equals(add.getMode(), Mode.getP())
                && add.getLeft() instanceof Proj array
                && add.getRight() instanceof Mul offset
                && (offset.getLeft() instanceof Conv conv || offset.getLeft() instanceof Const constant);
    }
}
