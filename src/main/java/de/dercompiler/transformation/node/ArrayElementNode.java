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
        if (!isPrepared(NodeAccess.METHOD_CALL_BASE)) {
            prepareGetObjectCallBase(state);
        }
        return new ObjectNode(getPreparedNode(NodeAccess.METHOD_CALL_BASE), ct);
    }

    @Override
    public ReferenceNode prepareAccessField(TransformationState state) {
        prepareNode(genLoadAndReset(state), NodeAccess.FIELD_ACCESS);
        return this;
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName) {
        ClassType ct = getTypeAsClass();
        if (!isPrepared(NodeAccess.FIELD_ACCESS)) prepareAccessField(state);
        return new ObjectNode(getPreparedNode(NodeAccess.FIELD_ACCESS), ct).accessField(state, fieldName);
    }

    @Override
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        prepareNode(genLoadAndReset(state), NodeAccess.METHOD_CALL_BASE);
        return this;
    }

    @Override
    public ReferenceNode prepareAccessArray(TransformationState state) {
        prepareNode(genLoadAndReset(state), NodeAccess.ARRAY_ACCESS);
        return this;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        if (!isPrepared(NodeAccess.ARRAY_ACCESS)) prepareAccessArray(state);
        return new ArrayNode(getPreparedNode(NodeAccess.ARRAY_ACCESS), at, at.getDimension()).accessArray(state, offset);
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
