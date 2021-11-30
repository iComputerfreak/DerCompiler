package de.dercompiler.ast.printer;

import de.dercompiler.ast.expression.*;

public interface ASTExpressionVisitor {

    void visitArrayAccess(ArrayAccess arrayAccess);
    void visitBooleanValue(BooleanValue booleanValue);
    void visitBinaryExpression(BinaryExpression binaryExpression);
    void visitErrorExpression(ErrorExpression errorExpression);
    void visitFieldAccess(FieldAccess fieldAccess);
    void visitIntegerValue(IntegerValue integerValue);
    void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression);
    void visitMethodInvocation(MethodInvocationOnObject methodInvocation);
    void visitNegativeExpression(NegativeExpression negativeExpression);
    void visitNewArrayExpression(NewArrayExpression newArrayExpression);
    void visitNewObjectExpression(NewObjectExpression newObjectExpression);
    void visitNullValue(NullValue nullValue);
    void visitPrimaryExpression(PrimaryExpression primaryExpression);
    void visitThisValue(ThisValue thisValue);
    void visitUninitializedValue(UninitializedValue uninitializedValue);
    void visitVariable(Variable variable);

}
