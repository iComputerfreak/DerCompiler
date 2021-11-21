package de.dercompiler.pass.passes;

import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an {@link ASTExpressionVisitor} that collects references to nodes that can be assigned a type. This is to separate the traversal of the Expression trees from the actual usage of the nodes, of which there may be various different ones.
 */
public class ReferencesCollector implements ASTExpressionVisitor {

    private final boolean collectVariables;
    private final boolean collectFieldAccesses;
    private final boolean collectMethodInvocations;
    private final boolean collectArrayAccesses;
    private final boolean collectAssignmentExpressions;
    private final boolean collectNewObjectExpressions;

    private ArrayList<Expression> expressions;

    public ReferencesCollector() {
        this(true, true, true, true, true, true);
    }

    public ReferencesCollector(boolean collectVariables, boolean collectFieldAccesses, boolean collectMethodInvocations, boolean collectArrayAccesses, boolean collectAssignmentExpressions, boolean collectNewObjectExpressions) {
        this.collectVariables = collectVariables;
        this.collectFieldAccesses = collectFieldAccesses;
        this.collectMethodInvocations = collectMethodInvocations;
        this.collectArrayAccesses = collectArrayAccesses;
        this.collectAssignmentExpressions = collectAssignmentExpressions;
        this.collectNewObjectExpressions = collectNewObjectExpressions;
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
        if (collectArrayAccesses) expressions.add(arrayAccess);
    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) {

    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLhs().accept(this);
        binaryExpression.getRhs().accept(this);
        if (binaryExpression instanceof AssignmentExpression ass && collectAssignmentExpressions)
            expressions.add(ass);
    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        fieldAccess.getEncapsulated().accept(this);
        if (collectFieldAccesses) expressions.add(fieldAccess);
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
        Expression refObj = methodInvocation.getEncapsulated();
        if (refObj == null) {
            expressions.add(new ThisValue(methodInvocation.getSourcePosition()));
        } else {
            refObj.accept(this);
        }
        Arguments arguments = methodInvocation.getArguments();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
        }
        if (collectMethodInvocations) expressions.add(methodInvocation);
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
        if (collectNewObjectExpressions) expressions.add(newObjectExpression);
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
        if (collectVariables) expressions.add(variable);
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {

    }


}
