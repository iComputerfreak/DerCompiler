package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.nodes.Node;

public class LocalVariableNode extends ReferenceNode {

    private int num;

    public LocalVariableNode(Type type, int num) {
        super(null, type);
        this.num = num;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return state.construction.getVariable(num, mode);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        state.construction.setVariable(num, value.genLoad(state));
        return value;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        Node elem_ptr = TransformationHelper.addOffsetToPointer(state, genLoad(state), offset);
        return new ArrayNode(elem_ptr, at.getElementType(), at.getDimension() - 1);
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName, Type fieldType) {
        ClassType ct = getTypeAsClass();
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), Utils.transformVariableIdentifier(fieldName));
        Node member = state.construction.newMember(ref , field);
        return new FieldNode(member, fieldType);
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        return new ObjectNode(genLoad(state), getTypeAsClass()); //error when type is no class
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
