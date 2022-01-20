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

import static de.dercompiler.lexer.token.OperatorToken.GREATER_THAN_EQUAL;

public final class GreaterEqualExpression extends ComparisonExpression {
    public GreaterEqualExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public OperatorToken getOperator() {
        return GREATER_THAN_EQUAL;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof GreaterEqualExpression gee) {
            return syntaxEqualLhsRhs(gee);
        }
        return false;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        return TransformationHelper.createComparator(state,this, Relation.GreaterEqual, getType());
    }
}
