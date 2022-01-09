package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.Mode;
import firm.nodes.Node;

public class ArrayNode extends ReferenceNode {

    private int dim;

    public ArrayNode(Node ref, Type type, int dimension) {
        super(ref, type);
        this.dim = dimension;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return ref;
    }


    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("ArrayNode is a r-Value");
        return null; //we never return
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        if (isElement()) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("invalid ArrayAccess on Array of Dimension 0");
        }
        Node elem_ptr = TransformationHelper.addOffsetToPointer(state, ref, offset);
        return new ArrayElementNode(elem_ptr, getElementType());
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName) {
        if (!isElement()) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("invalid ArrayAccess on Array of Dimension: " + dim);
        }
        ClassType ct = getTypeAsClass();
        FieldDefinition def = ct.getField(fieldName);
        Type fieldType = def.getReferenceType();
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), def.getNode().getMangledIdentifier());
        Node member = state.construction.newMember(ref , field);
        return new FieldNode(member, fieldType);
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        if (!isElement()) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("invalid MethodCall on Array of Dimension: " + dim);
        }
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct);
    }

    private firm.Type type() {
        return this.type.getFirmTransformationType();
    }

    public Node getPointer() {
        return ref;
    }

    public Type getElementType() {
        return type;
    }

    public int getDimension() {
        return dim;
    }

    public boolean isElement() {
        return dim == 0;
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
