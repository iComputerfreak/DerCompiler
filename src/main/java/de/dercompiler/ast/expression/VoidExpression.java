package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

import java.util.Objects;

public final class VoidExpression extends Expression {

    public VoidExpression(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof VoidExpression;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitVoidExpression(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        //TODO: check with others do we use this and what do we do in this case?
        return null;
    }
}
