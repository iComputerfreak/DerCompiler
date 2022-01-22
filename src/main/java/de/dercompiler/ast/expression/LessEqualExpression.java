package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Relation;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.LESS_THAN;
import static de.dercompiler.lexer.token.OperatorToken.LESS_THAN_EQUAL;

public final class LessEqualExpression extends ComparisonExpression {
    public LessEqualExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public OperatorToken getOperator() {
        return LESS_THAN_EQUAL;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LessEqualExpression lee) {
            return syntaxEqualLhsRhs(lee);
        }
        return false;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        return TransformationHelper.createComparator(state, this, Relation.LessEqual, getType());
    }
}
