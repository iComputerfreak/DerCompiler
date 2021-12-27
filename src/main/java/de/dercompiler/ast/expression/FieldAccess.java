package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.FieldNode;
import de.dercompiler.transformation.node.ObjectNode;
import de.dercompiler.transformation.node.ReferenceNode;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.Type;
import firm.nodes.Node;

import java.util.Objects;

public final class FieldAccess extends PostfixExpression {

    String fieldName;
    public FieldAccess(SourcePosition position, Expression encapsulated, String fieldName) {
        super(position, encapsulated);
        this.fieldName = fieldName;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof FieldAccess fa) {
            return fieldName.equals(fa.fieldName) && syntaxEqualEncapsulated(fa);
        }
        return false;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitFieldAccess(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        ReferenceNode objRef = encapsulated.createNode(state);
        return objRef.accessField(state, fieldName);
    }

    public ReferenceNode errorNoValidFieldAccess(de.dercompiler.semantic.type.Type type, ReferenceNode objRef) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Error while generating FieldAccess, we got: " + type.getClass().getName() + " and " + objRef.getClass().getName());
        return null;
    }
}
