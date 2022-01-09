package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
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
        ReferenceNode res = null;
        Block current = state.construction.getCurrentBlock();
        if (state.expectValue()) {
            state.pushBranches(state.construction.newBlock(), state.construction.newBlock());
        }
        state.pushExpectBranch();
        Block and = state.construction.newBlock();
        Block trueBlock = state.exchangeTrueBlock(and);
        getLhs().createNode(state);
        state.exchangeTrueBlock(trueBlock);
        state.construction.setCurrentBlock(and);
        getRhs().createNode(state);
        and.mature();
        state.popExpect();
        state.construction.setCurrentBlock(current);
        if (state.expectValue()) {
            Block after = state.construction.newBlock();
            state.construction.setCurrentBlock(state.trueBlock());
            TransformationHelper.createDirectJump(state, after);
            state.construction.setCurrentBlock(state.falseBlock());
            TransformationHelper.createDirectJump(state, after);
            after.mature();
            state.construction.setCurrentBlock(after);
            res = new RValueNode(state.construction.newPhi(new Node[]{TransformationHelper.createBooleanNode(state, true), TransformationHelper.createBooleanNode(state, false)}, Mode.getBu()), getType());
            state.popBranches();
        }
        return res;
    }
}
