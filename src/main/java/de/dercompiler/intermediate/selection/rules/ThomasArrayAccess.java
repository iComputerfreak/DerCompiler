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

public class ThomasArrayAccess extends LoadRule {

    @Override
    public int getCost() {
        return 1 + getAnnotation(getAddnode().getLeft()).getCost();
    }


    private Operand getArray() {
        return getAnnotation(getAddnode().getLeft()).getDefinition();
    }


    private Add getAddnode(){
        return (Add) getRootNode().getPred(1);
    }

    private NodeAnnotation<?> getLeft(){
        return getAnnotation(getAddnode().getLeft());
    }

    private NodeAnnotation<?> getRight(){
        return getAnnotation(getAddnode().getRight());
    }

    /**
     * The former Mul node of the ArrayAccess subtree that contains the (index * scale) portion of the array access.
     *
     * @return the Const child node
     */
    private Const getOffset() {
        NodeAnnotation<?> op2 = getRight();
        if (op2.getRootNode() instanceof Const offset) {
            return offset;
        }
        // we never return
        throw new RuntimeException();
    }

    @Override
    public List<Operation> substitute() {
        int offset = getOffset().getTarval().asInt();
        Address target;

        // Load eff. address into vReg
        Operand targetAddr = getDefinition();

        List<Operation> ops;

        if (getArray() == null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Node %s has no target yet, so better implement a basic rule for it.".formatted(getLeft().getRootNode().toString()));
        }
        Operand arrayAddr = getArray();

        // There is nowhere to save the relative address, so calculate it again

        ops = new LinkedList<>();
        target = Address.loadWithOffset(arrayAddr, offset);


        if (targetAddr == null) {
            targetAddr = new VirtualRegister();
            setDefinition(Address.loadOperand(targetAddr));
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
        return nodes;
    }

    @Override
    public boolean matches(Load load) {
        return load != null
                && load.getPred(1) instanceof Add add
                && Objects.equals(add.getMode(), Mode.getP())
                && add.getLeft() instanceof Proj array
                && add.getRight() instanceof Const offset;
    }
}
