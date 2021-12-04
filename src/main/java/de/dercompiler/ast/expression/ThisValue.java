package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class ThisValue extends PrimaryExpression {

    public ThisValue(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof ThisValue;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitThisValue(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        return state.construction.newProj(state.graph.getArgs(), Mode.getP(), 0);
    }

    @Override
    public String toString() {
        return "this";
    }
}
