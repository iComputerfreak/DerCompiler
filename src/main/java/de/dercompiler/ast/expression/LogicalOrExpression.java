package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.OR_LAZY;

public final class LogicalOrExpression extends BinaryExpression {

    public LogicalOrExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalOrExpression loe) {
            return syntaxEqualLhsRhs(loe);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return OR_LAZY;
    }

    @Override
    public Node createNode(TransformationState state) {
        if (!state.isCondition()) {
            TransformationHelper.createConditionError();
        }
        Block or = state.construction.newBlock();
        Block current = state.construction.getCurrentBlock();
        Block falseB = state.exchangeFalseBlock(or);
        getLhs().createNode(state);
        state.exchangeFalseBlock(falseB);
        state.construction.setCurrentBlock(or);
        getRhs().createNode(state);
        or.mature();
        state.construction.setCurrentBlock(current);
        return null;
    }
}
