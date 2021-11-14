package de.dercompiler.ast.printer;

import de.dercompiler.ast.expression.*;

public interface ASTExpressionVisitor {

    void visitArrayAccess(ArrayAccess arrayAccess);
    void visitBinaryExpression(BinaryExpression binaryExpression);
    void visitErrorExpression(ErrorExpression errorExpression);
    void visitExpression(Expression expression);
    void visitFieldAccess(FieldAccess fieldAccess);
    void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression);
    void visitMethodInvocation(MethodInvocationOnObject methodInvocation);
    void visitNegativeExpression(NegativeExpression negativeExpression);
    void visitNewArrayExpression(NewArrayExpression newArrayExpression);
    void visitNewObjectExpression(NewObjectExpression newObjectExpression);
    void visitPrimaryExpression(PrimaryExpression primaryExpression);
    void visitVoidExpression(VoidExpression voidExpression);

}
