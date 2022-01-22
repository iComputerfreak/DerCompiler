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
    public ReferenceNode prepareLoad(TransformationState state) {
        prepareNode(state.construction.getVariable(num, mode), NodeAccess.LOAD);
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
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        return new ObjectNode(genLoad(state), getTypeAsClass()).prepareGetObjectCallBase(state);
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
