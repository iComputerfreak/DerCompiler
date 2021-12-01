package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class IntegerValue extends PrimaryExpression {

    private final String value;

    /**
        True if an odd number of NegativeExpressions directly surrounds this IntegerValue. Used for type checking.
     */
    private boolean negative;

    /**
     *  This value reflects the unsigned value of this integer literal and is thus non-negative;
     *  only if the value is -MIN_VALUE, then it is set to MIN_VALUE.
     */
    private int unsignedValue;

    public IntegerValue(SourcePosition position, String value) {
        super(position);
        this.value = value;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof IntegerValue iv) {
            return value.equals(iv.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitIntegerValue(this);
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean b) {
        this.negative = b;
    }

    public void setValue(int value) {
        this.unsignedValue = value;
    }

    @Override
    public Node createNode(TransformationState state) {
        int value;

        if (this.unsignedValue == Integer.MIN_VALUE && this.negative) {
            value = Integer.MIN_VALUE;
        } else {
            value = (negative ? -1 : 1) * unsignedValue;
        }
        return state.construction.newConst(value, Mode.getIs());
    }
}
