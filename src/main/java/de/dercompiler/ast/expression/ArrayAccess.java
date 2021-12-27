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
        ReferenceNode base_ptr = getEncapsulated().createNode(state);
        if (!(base_ptr instanceof ArrayNode an)) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("we make a ArrayAccess on: " + encapsulated + " this should have been found by semantic!");
            return null;
        }

        int type_size_const = an.getElementType().getFirmType().getSize();
        Node type_size = state.construction.newConst(type_size_const, Mode.getIu());
        ReferenceNode elements = index.createNode(state);
        //convert size?
        Node offset = TransformationHelper.calculateOffset(state, type_size, elements.genLoad(state));
        Node elem_ptr = TransformationHelper.addOffsetToPointer(state, an.getPointer(), offset);
        return new ArrayNode(elem_ptr, an.getElementType(), an.getDimension() - 1);
    }
}
