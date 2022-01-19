package de.dercompiler.ast.expression;

import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.nodes.Node;

public abstract sealed class BinaryExpression extends Expression permits AddExpression, AssignmentExpression, ComparisonExpression, DivisionExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression {

    private final Expression lhs;
    private final Expression rhs;

    public BinaryExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected boolean syntaxEqualLhsRhs(BinaryExpression other) {
        return lhs.syntaxEquals(other.lhs) && rhs.syntaxEquals(other.rhs);
    }

    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public abstract OperatorToken getOperator();

    public ReferenceNode createLhs(TransformationState state) {
        if (state.isAsignement) {
            state.pushExpectValue();
        }
        ReferenceNode nodeLhs = lhs.createNode(state);
        if (state.isAsignement) {
            state.popExpect();
        }
        return nodeLhs;
    }

    public ReferenceNode createRhs(TransformationState state) {
        return rhs.createNode(state);
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitBinaryExpression(this);
    }
}
