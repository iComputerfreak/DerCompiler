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

        //TODO fix, this should not return null, but we need to decide where we store class_pointer
        return state.construction.newConst(0, Mode.getP());
    }

    @Override
    public String toString() {
        return "this";
    }
}
