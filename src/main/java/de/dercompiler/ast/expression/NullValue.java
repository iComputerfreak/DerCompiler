package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.NullType;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class NullValue extends PrimaryExpression {


    public NullValue(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof NullValue;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNullValue(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        if (!(getType() instanceof NullType nt)) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Null has no Null-Type");
            return null;
        }
        return new RValueNode(state.construction.newConst(0, Mode.getP()), nt.getExpectedType());
    }

    @Override
    public String toString() {
        return "null";
    }
}
