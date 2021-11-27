package de.dercompiler.transformation;

import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;

public class ExpressionTransformation implements ASTExpressionVisitor {

    TransformationState state;

    public ExpressionTransformation(TransformationState state) {
        this.state = state;
    }

    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {

    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) {
        state.lhs =
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {

    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {

    }

    @Override
    public void visitIntegerValue(IntegerValue integerValue) {

    }

    @Override
    public void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression) {

    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject methodInvocation) {

    }

    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {

    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {

    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {

    }

    @Override
    public void visitNullValue(NullValue nullValue) {

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {

    }

    @Override
    public void visitThisValue(ThisValue thisValue) {

    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {

    }

    @Override
    public void visitVariable(Variable variable) {

    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {

    }
}
