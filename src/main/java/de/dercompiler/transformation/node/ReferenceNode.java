package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
import firm.Entity;
import firm.Mode;
import firm.nodes.Node;

public abstract class ReferenceNode {

    protected Node ref;
    protected Mode mode;
    protected Type type;
    private PreparedNode preparedNode;

    public ReferenceNode(Node ref, Type type) {
        this.ref = ref;
        this.type = type;
        this.mode = type.getFirmTransformationType().getMode();
        this.preparedNode = new PreparedNode();
    }

    public Node genLoad(TransformationState state) {
        if (!isPrepared(NodeAccess.LOAD)) {
            prepareLoad(state);
        }
        return getPreparedNode(NodeAccess.LOAD);
    }

    public abstract ReferenceNode genStore(TransformationState state, ReferenceNode value);

    public abstract ReferenceNode prepareLoad(TransformationState state);

    public ReferenceNode prepare(TransformationState state, NodeAccess access) {
        return switch (access) {
            case LOAD -> prepareLoad(state);
            case ARRAY_ACCESS -> prepareAccessArray(state);
            case FIELD_ACCESS -> prepareAccessField(state);
            case METHOD_CALL_BASE -> prepareGetObjectCallBase(state);
            default -> {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("wanted to prepare access: " + access + "but not possible");
                yield null; //we never return
            }
        };
    }

    public ReferenceNode prepareAccessArray(TransformationState state) {
        prepareNode(ref, NodeAccess.ARRAY_ACCESS);
        return this;
    }

    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        if (!isPrepared(NodeAccess.ARRAY_ACCESS)) prepareAccessArray(state);
        return new ArrayNode(getPreparedNode(NodeAccess.ARRAY_ACCESS), at.getElementType(), at.getDimension()).accessArray(state, offset);
    }

    public ReferenceNode prepareAccessField(TransformationState state) {
        //gen load but mark as AccessArray
        prepareNode(genLoad(state), NodeAccess.FIELD_ACCESS);
        return this;
    }

    public ReferenceNode accessField(TransformationState state, String fieldName) {
        ClassType ct = getTypeAsClass();
        FieldDefinition def = ct.getField(fieldName);
        Type fieldType = def.getType();
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), def.getNode().getMangledIdentifier());
        if (!isPrepared(NodeAccess.FIELD_ACCESS)) prepareAccessField(state);
        Node member = state.construction.newMember(getPreparedNode(NodeAccess.FIELD_ACCESS), field);
        return new FieldNode(member, fieldType);
    }
    public abstract ReferenceNode prepareGetObjectCallBase(TransformationState state);
    public abstract ObjectNode getObjectCallBase(TransformationState state);

    public abstract boolean isReference();

    public Node getReference() {
        return ref;
    }

    public Mode getMode() {
        return mode;
    }

    public Type getType() {
        return type;
    }

    protected boolean isPrepared(NodeAccess access) {
        return preparedNode.isPrepared(access);
    }

    protected Node getPreparedNode(NodeAccess access) {
        return preparedNode.getPrepared(access);
    }

    protected void prepareNode(Node node, NodeAccess access) {
        preparedNode.prepare(node, access);
    }

    protected ClassType getTypeAsClass() {
        if (!(type instanceof ClassType ct)) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Can't cast type: " + type.getClass().getName() + " to " + ClassType.class.getName());
            return null; //we never return
        }
        return ct;
    }

    protected ArrayType getTypeAsArray() {
        if (!(type instanceof ArrayType at)) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Can't cast type: " + type.getClass().getName() + " to " + ArrayType.class.getName());
            return null; //we never return
        }
        return at;
    }
}
