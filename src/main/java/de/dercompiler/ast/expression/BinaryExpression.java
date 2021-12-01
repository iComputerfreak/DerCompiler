package de.dercompiler.ast.expression;

import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

public abstract sealed class BinaryExpression extends Expression permits AssignmentExpression, AddExpression, DivisionExpression, EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression, NotEqualExpression {

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

    public void createChildNodes(TransformationState state) {
        Node nodeLhs = lhs.createNode(state);
        state.rhs = rhs.createNode(state);
        state.lhs = nodeLhs;
    }

    public void clearChildNodes(TransformationState state) {
        state.lhs = null;
        state.rhs = null;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitBinaryExpression(this);
    }
}
