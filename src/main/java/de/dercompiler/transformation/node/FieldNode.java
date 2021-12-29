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

public class FieldNode extends ReferenceNode {


    public FieldNode(Node ref, Type type) {
        super(ref, type);
        this.type = type;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return TransformationHelper.genLoad(state, ref, mode);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), type.getFirmTransformationType());
        return value;
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        return new ObjectNode(ref, getTypeAsClass()); //checks for  class-type
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
