package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.lexer.SourcePosition;

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
}
