package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.TransformationState;
import firm.Entity;
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
    public Node createNode(TransformationState state) {
        Node obj = encapsulated.createNode(state);
        if (!(encapsulated.getType() instanceof ClassType ct)) return errorNoValidFieldAccess();
        FieldDefinition def = ct.getField(fieldName);
        Entity field = state.globalScope.getMemberEntity(ct.getIdentifier(), fieldName);
        return state.construction.newMember(obj, field);
    }

    public Node errorNoValidFieldAccess() {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Error while generating FieldAccess");
        return null;
    }
}
