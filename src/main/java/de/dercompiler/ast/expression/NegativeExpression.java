package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class NegativeExpression extends UnaryExpression {

    /**
     * True if an odd number of NegativeExpressions surround this NegativeExpression, which in turn would make its content positive.
     */
    private boolean negative;

    public NegativeExpression(SourcePosition position, Expression encapsulated) {
        super(position, encapsulated);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NegativeExpression ne) {
            return syntaxEqualEncapsulated(ne);
        }
        return false;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNegativeExpression(this);
    }

    public void setNegative(boolean b) {
        this.negative = b;
    }

    public boolean isNegative() {
        return negative;
    }
}
