package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.nodes.Node;

import java.util.Objects;

public final class NegativeExpression extends UnaryExpression {

    /**
     * True if this expression is encapsulated in a NegativeExpression (ignoring _how many_ NegativeExpressions there may be around it)
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
    public ReferenceNode createNode(TransformationState state) {
        ReferenceNode inner = getEncapsulated().createNode(state);
        if (getEncapsulated() instanceof IntegerValue iv && iv.isNegative()) {
            // minuses on constant values must be eliminated (MIN_VALUE problem)
            return inner;
        }
        return new RValueNode(state.construction.newMinus(inner.genLoad(state)), getType());
    }

    public void setNegative(boolean b) {
        this.negative = b;
    }

    public boolean isNegative() {
        return negative;
    }
}
