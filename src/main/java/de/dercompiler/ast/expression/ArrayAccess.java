package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
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
    public Node createNode(TransformationState state) {
        Node base_ptr = getEncapsulated().createNode(state);
        //TODO getTypeSize
        Node type_size = state.construction.newConst(0, Mode.getIu());
        Node elements = index.createNode(state);
        Node index = TransformationHelper.calculateOffset(state, type_size, elements);
        Node elem_ptr = TransformationHelper.addOffsetToPointer(state, base_ptr, index);
        Mode mode = null; //TODO: how to get mode?
        return TransformationHelper.genLoad(state, elem_ptr, mode);
    }
}
