package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

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

    @Override
    public Node createNode(TransformationState state) {
        Node inner = getEncapsulated().createNode(state);
        if (getEncapsulated() instanceof IntegerValue) {
            // minuses on constant values must be eliminated (MIN_VALUE problem)
            return inner;
        }
        return state.construction.newMinus(inner);
    }

    public void setNegative(boolean b) {
        this.negative = b;
    }

    public boolean isNegative() {
        return negative;
    }
}
