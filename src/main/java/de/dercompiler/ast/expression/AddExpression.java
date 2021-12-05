package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.PLUS;

public final class AddExpression extends BinaryExpression {

    public AddExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (this == other) return true;
        if (other instanceof AddExpression ae) {
            return syntaxEqualLhsRhs(ae);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return PLUS;
    }

    @Override
    public Node createNode(TransformationState state) {
        createChildNodes(state);
        Node res = state.construction.newAdd(state.lhs, state.rhs);
        clearChildNodes(state);
        return res;
    }
}
