package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.Relation;
import firm.nodes.Cond;
import firm.nodes.Jmp;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.EQUAL;

public final class EqualExpression extends BinaryExpression {

    public EqualExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof EqualExpression ee) {
            return syntaxEqualLhsRhs(ee);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return EQUAL;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        createChildNodes(state);
        Node cmp = TransformationHelper.createComp(state, Relation.Equal);
        if (state.isCondition()) {
            TransformationHelper.createConditionJumps(state, cmp);
        }
        clearChildNodes(state);
        return null;
    }
}
