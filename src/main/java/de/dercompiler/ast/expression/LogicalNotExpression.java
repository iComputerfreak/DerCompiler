package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.nodes.Node;

import java.util.Objects;

public final class LogicalNotExpression extends UnaryExpression {

    public LogicalNotExpression(SourcePosition position, Expression encapsulated) {
        super(position, encapsulated);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalNotExpression lne) {
            return syntaxEqualEncapsulated(lne);
        }
        return false;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitLogicalNotExpression(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        if (!state.isCondition()) {
            TransformationHelper.createConditionError();
        }
        state.swapTrueFalseBlock();
        encapsulated.createNode(state);
        state.swapTrueFalseBlock();
        return null;
    }
}
