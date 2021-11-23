package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class BooleanValue extends PrimaryExpression {

    private final boolean value;

    public BooleanValue(SourcePosition position, boolean value) {
        super(position);
        this.value = value;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof BooleanValue bv) {
            return value == bv.value;
        }
        return false;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitBooleanValue(this);
    }
}
