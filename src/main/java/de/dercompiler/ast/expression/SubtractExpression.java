package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.MINUS;

public final class SubtractExpression extends BinaryExpression {

    public SubtractExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof SubtractExpression se) {
            return syntaxEqualLhsRhs(se);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return MINUS;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        createChildNodes(state);
        Node res = state.construction.newSub(state.lhs.genLoad(state), state.rhs.genLoad(state));
        Mode resMode = TransformationHelper.unifyMode(state.lhs.getMode(), state.rhs.getMode());
        clearChildNodes(state);
        return new RValueNode(res, getType());
    }
}
