package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.STAR;

public final class MultiplyExpression extends BinaryExpression {

    public MultiplyExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof MultiplyExpression me) {
            return syntaxEqualLhsRhs(me);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return STAR;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        createChildNodes(state);
        Node res = state.construction.newMul(state.lhs.genLoad(state), state.rhs.genLoad(state));
        clearChildNodes(state);
        return new RValueNode(res, getType());
    }
}
