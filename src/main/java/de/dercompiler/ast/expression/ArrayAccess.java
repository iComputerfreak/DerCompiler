package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.transformation.FirmTypes;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.nodes.Node;

import java.util.Objects;

public final class ArrayAccess extends PostfixExpression {

    private final Expression index;

    public ArrayAccess(SourcePosition position, Expression encapsulated, Expression index) {
        super(position, encapsulated);
        this.index = index;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ArrayAccess aa) {
            return index.syntaxEquals(aa.index) && syntaxEqualEncapsulated(aa);
        }
        return false;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitArrayAccess(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        state.pushExpectValue();
        ReferenceNode base_ptr = getEncapsulated().createNode(state);
        state.popExpect();
        if (!(base_ptr.getType() instanceof ArrayType at)) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("NewArrayExpression has no ArrayType");
            return null; //we never return
        }
        base_ptr = base_ptr.prepareAccessArray(state);

        int type_size_const = at.getElementType().getFirmTransformationType().getSize();
        Node type_size = state.construction.newConst(type_size_const, FirmTypes.longFirmType.getMode());
        ReferenceNode elements = index.createNode(state);
        //convert size?
        Node offset = TransformationHelper.calculateOffset(state, type_size, elements.genLoad(state));
        ReferenceNode res = base_ptr.accessArray(state, offset);
        if (!state.expectValue()) {
            TransformationHelper.booleanValueToConditionalJmp(state, res.genLoad(state));
            return null;
        }
        return res;
    }
}
