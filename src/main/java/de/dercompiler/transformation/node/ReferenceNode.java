package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public abstract class ReferenceNode {

    protected Node ref;
    protected Mode mode;
    protected Type type;

    public ReferenceNode(Node ref, Type type) {
        this.ref = ref;
        this.type = type;
        this.mode = type.getFirmTransformationType().getMode();
    }

    public abstract Node genLoad(TransformationState state);
    public abstract ReferenceNode genStore(TransformationState state, ReferenceNode value);

    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(ref, at.getElementType(), at.getDimension()).accessArray(state, offset);
    }

    public ReferenceNode accessField(TransformationState state, String fieldName) {
        ClassType ct = getTypeAsClass();
        FieldDefinition def = ct.getField(fieldName);
        Type fieldType = def.getReferenceType();
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), Utils.transformVariableIdentifier(fieldName));
        Node member = state.construction.newMember(ref , field);
        return new FieldNode(member, fieldType);
    }
    public abstract ObjectNode getObjectCallBase(TransformationState state);

    public abstract boolean isReference();

    public Mode getMode() {
        return mode;
    }

    public Type getType() {
        return type;
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
