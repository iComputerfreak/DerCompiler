package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.CondTarget;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TransformationWarrningIds;
import firm.Graph;
import firm.nodes.Add;
import firm.nodes.Node;

import java.util.List;

public class ArrayOffsetRule extends SubstitutionRule<Add> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    Add getAdd() {
        return getRootNode();
    }

    NodeAnnotation<?> getLeft() {
        return getAnnotation(getAdd().getLeft());
    }

    NodeAnnotation<?> getRight() {
        return getAnnotation(getAdd().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operand target;
        if (!hasDefinition()) {
            target = new VirtualRegister();
            setDefinition(Address.loadOperand(target));
        } else {
            target = ((Address) getDefinition()).getBase();
        }

        if (getLeft().getDefinition() == Address.NULL_PTR || getRight().getDefinition() == Address.NULL_PTR) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).printWarning(TransformationWarrningIds.NULL_REFERENCE, "This program references a static null pointer at node" + node);
            setDefinition(Address.NULL_PTR);
        }

        Operation lea = new de.dercompiler.intermediate.operation.BinaryOperations.Lea(target, new Address(0, getLeft().getDefinition(), getRight().getDefinition(), 1));
        lea.setMode(getRootNode().getMode());
        setMode(getRootNode().getMode());

        return List.of(lea);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }


    @Override
    public boolean matches(Add add) {
        return add.getMode().equals(firm.Mode.getP());
    }

}
