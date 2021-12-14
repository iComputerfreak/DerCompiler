package de.dercompiler.transformation.node;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.Type;
import firm.nodes.Node;

public class ObjectNode extends ReferenceNode{

    private Type type;

    public ObjectNode(Node ref, Type type) {
        super(ref, Mode.getP());
        this.type = type;
    }

    @Override
    public Node genLoad(TransformationState state) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("no load on classes allowed, we only work on references");
        return ref;
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("no store on classes allowed, we only work on references");
        return value;
    }

    public Node getBase() {
        return ref;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
