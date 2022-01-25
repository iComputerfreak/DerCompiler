package de.dercompiler.transformation.node;

import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

public class ArrayElementNode extends ReferenceNode {


    public ArrayElementNode(Node ref, Type type) {
        super(ref, type);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), value.getType().getFirmTransformationType());
        return value;
    }

    @Override
    public ReferenceNode prepareLoad(TransformationState state) {
        prepareNode(TransformationHelper.genLoad(state, ref, type.getFirmTransformationType().getMode()), NodeAccess.LOAD);
        return this;
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct);
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName) {
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct).accessField(state, fieldName);
    }

    @Override
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        prepareNode(ref, NodeAccess.METHOD_CALL_BASE);
        return this;
    }

    @Override
    public ReferenceNode prepareAccessArray(TransformationState state) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(genLoad(state), at.getElementType(), at.getDimension()).prepareAccessArray(state);
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(genLoad(state), at.getElementType(), at.getDimension()).accessArray(state, offset);
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
