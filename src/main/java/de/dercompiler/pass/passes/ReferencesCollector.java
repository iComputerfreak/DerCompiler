package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;

import java.util.ArrayList;
import java.util.List;

public class ReferencesCollector implements ASTExpressionVisitor {

    private ArrayList<Expression> expressions;

    public ReferencesCollector() {

    }

    public List<Expression> analyze(Expression ex) {
        expressions = new ArrayList<>();
        ex.accept(this);
        return expressions;
    }

    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {
        arrayAccess.getEncapsulated().accept(this);
        arrayAccess.getIndex().accept(this);
        expressions.add(arrayAccess);
    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) {

    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLhs().accept(this);
        binaryExpression.getRhs().accept(this);
    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        fieldAccess.getEncapsulated().accept(this);
        expressions.add(fieldAccess);
    }

    @Override
    public void visitIntegerValue(IntegerValue integerValue) {

    }

    @Override
    public void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression) {
        logicalNotExpression.getEncapsulated().accept(this);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject methodInvocation) {
        methodInvocation.getEncapsulated().accept(this);
        Arguments arguments = methodInvocation.getArguments();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
        }
        expressions.add(methodInvocation);
    }

    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {
        negativeExpression.getEncapsulated().accept(this);
    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {
        newArrayExpression.getSize().accept(this);
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
        expressions.add(thisValue);
    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {

    }

    @Override
    public void visitVariable(Variable variable) {
        expressions.add(variable);
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {

    }


}
