package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

import java.util.Objects;

public final class UninitializedValue extends Expression {

    public UninitializedValue(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof UninitializedValue;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitUninitializedValue(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        //TODO return 0? do nothing, because we don't create the variable at this point?
        return null;
    }

}
