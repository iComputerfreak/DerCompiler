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
        return genRefLoad(state).genLoad(state);
    }

    public ReferenceNode genRefLoad(TransformationState state) {
        Node res = TransformationHelper.genLoad(state, ref, isElement() ? mode : Mode.getP());
        if (isElement()) {
            if (type instanceof ClassType ct) {
                return new ObjectNode(res, ct);
            }
            return new RValueNode(res, type);
        }
        return new ArrayNode(res, type, dim - 1);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), isElement() ? type() : Mode.getP().getType());
        return value;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        if (!isElement()) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("invalid ArrayAccess on Array of Dimension 0");
        }
        Node elem_ptr = TransformationHelper.addOffsetToPointer(state, ref, offset);
        return new ArrayNode(elem_ptr, getElementType(), getDimension() - 1);
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName) {
        if (!isElement()) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("invalid ArrayAccess on Array of Dimension: " + dim);
        }
        ClassType ct = getTypeAsClass();
        FieldDefinition def = ct.getField(fieldName);
        Type fieldType = def.getReferenceType();
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), Utils.transformVariableIdentifier(fieldName));
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
