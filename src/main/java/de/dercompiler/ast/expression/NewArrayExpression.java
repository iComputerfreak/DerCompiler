package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class NewArrayExpression extends PrimaryExpression {

    private final BasicType type;
    private final Expression size;
    private final int dimension;

    public NewArrayExpression(SourcePosition position, BasicType type, Expression size, int dimension) {
        super(position);
        this.type = type;
        this.size = size;
        this.dimension = dimension;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewArrayExpression nae) {
            return type.syntaxEquals(nae.type)
                    && dimension == nae.dimension
                    && size.syntaxEquals(nae.size);
        }
        return false;
    }

    public BasicType getBasicType() {
        return type;
    }

    public Expression getSize() {
        return size;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNewArrayExpression(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();

        //TODO getTypeSize();
        Node type_size = state.construction.newConst(1, Mode.getIu());
        //TODO getAlignment in bit or byte? 8byte because of 64bit?
        int align = 0;
        return state.construction.newAlloc(mem, TransformationHelper.calculateSize(state, type_size, getSize().createNode(state)), align);
    }
}
