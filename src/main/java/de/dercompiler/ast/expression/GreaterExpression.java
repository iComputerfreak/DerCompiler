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

import static de.dercompiler.lexer.token.OperatorToken.GREATER_THAN;

public final class GreaterExpression extends BinaryExpression {
    public GreaterExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public OperatorToken getOperator() {
        return GREATER_THAN;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof GreaterExpression ge) {
            return syntaxEqualLhsRhs(ge);
        }
        return false;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        state.pushExpectValue();
        createChildNodes(state);
        ReferenceNode res = TransformationHelper.createComparator(state, Relation.Greater);
        clearChildNodes(state);
        state.popExpect();
        return res;
    }
}
