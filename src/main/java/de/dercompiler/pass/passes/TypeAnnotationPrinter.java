package de.dercompiler.pass.passes;

import de.dercompiler.ast.MainMethod;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.PrettyPrinter;

public class TypeAnnotationPrinter extends PrettyPrinter {

    public TypeAnnotationPrinter(boolean strictParenthesis) {
        super(strictParenthesis);
    }

    @Override
    public void visitMainMethod(MainMethod main) {

    }

    @Override
    public void visitIntegerValue(IntegerValue integerValue) {
        super.visitIntegerValue(integerValue);
        printTypeAnnotation(integerValue);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject invocation) {
        super.visitMethodInvocation(invocation);
        printTypeAnnotation(invocation);
    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        super.visitFieldAccess(fieldAccess);
        printTypeAnnotation(fieldAccess);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpr) {
        super.visitBinaryExpression(binaryExpr);
        printTypeAnnotation(binaryExpr);
    }

    @Override
    public void visitNegativeExpression(NegativeExpression neg) {
        super.visitNegativeExpression(neg);
        printTypeAnnotation(neg);
    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {
        super.visitPrimaryExpression(primaryExpression);
        printTypeAnnotation(primaryExpression);
    }

    @Override
    public void visitVariable(Variable variable) {
        super.visitVariable(variable);
        printTypeAnnotation(variable);
    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression consArray) {
        super.visitNewArrayExpression(consArray);
        printTypeAnnotation(consArray);
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression cons) {
        super.visitNewObjectExpression(cons);
        printTypeAnnotation(cons);
    }

    private void printTypeAnnotation(Expression expr) {
        append("{");
        append(expr.getType().toString());
        append("}");
    }


}
