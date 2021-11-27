package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

import java.util.Objects;

public final class ErrorExpression extends Expression {

    public ErrorExpression(SourcePosition position) { super(position); }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof ErrorExpression ee;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitErrorExpression(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM)
                .internalError("We can't create a Expression for a miss-formed Expression, theres something wrong, we shouldn't even get to this Point!");
        return null;
    }
}
