package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

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
}
