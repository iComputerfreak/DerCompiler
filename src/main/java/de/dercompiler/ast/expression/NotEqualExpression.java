package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Relation;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.NOT_EQUAL;

public final class NotEqualExpression extends BinaryExpression {
    public NotEqualExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NotEqualExpression nee) {
            return syntaxEqualLhsRhs(nee);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return NOT_EQUAL;
    }

    @Override
    public Node createNode(TransformationState state) {
        state.swapTrueFalseBlock();
        createChildNodes(state);
        Node cmp = TransformationHelper.createComp(state, Relation.Equal);
        if (state.isCondition()) {
            TransformationHelper.createConditionJumps(state, cmp);
        }
        clearChildNodes(state);
        state.swapTrueFalseBlock();
        return cmp;
    }
}
