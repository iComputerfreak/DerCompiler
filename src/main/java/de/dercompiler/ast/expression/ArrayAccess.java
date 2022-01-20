package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ArrayNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.ArrayType;
import firm.Mode;
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
        ReferenceNode base_ptr = getEncapsulated().createNode(state).prepareAccessArray(state);
        base_ptr.accessArray(state, state.construction.newConst(0, Mode.getIs()));

        int type_size_const = base_ptr.getType().getFirmType().getSize();
        Node type_size = state.construction.newConst(type_size_const, Mode.getIu());
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
