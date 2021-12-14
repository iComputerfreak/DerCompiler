package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.AND_LAZY;

public final class LogicalAndExpression extends BinaryExpression {

    public LogicalAndExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalAndExpression lae) {
            return syntaxEqualLhsRhs(lae);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return AND_LAZY;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        if (!state.isCondition()) {
            TransformationHelper.createConditionError();
        }
        Block and = state.construction.newBlock();
        Block current = state.construction.getCurrentBlock();
        Block trueBlock = state.exchangeTrueBlock(and);
        getLhs().createNode(state);
        state.exchangeTrueBlock(trueBlock);
        state.construction.setCurrentBlock(and);
        getRhs().createNode(state);
        and.mature();
        state.construction.setCurrentBlock(current);
        return null;
    }
}
