package de.dercompiler.pass.passes;

import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This class represents an {@link ASTExpressionVisitor} that collects references to nodes that can be assigned a type.
 * This is to separate the traversal of the Expression trees from the actual usage of the nodes, of which there may be various different ones.
 */
public class ReferencesCollector implements ASTExpressionVisitor {

    private EnumSet<ReferenceType> types;

    private ArrayList<Expression> expressions;
    private boolean visitInternal;


    enum ReferenceType {
        VARIABLE, FIELD, METHOD_INVOCATION, ARRAY_ACCESS, ASSIGNMENT_EXPRESSION, NEW_OBJECT, NEW_ARRAY, NULL_VALUE, THIS, UNINITIALIZED_VALUE
    }

    public ReferencesCollector() {
        types = EnumSet.allOf(ReferenceType.class);
    }

    public ReferencesCollector(ReferenceType... types) {
         this.types = EnumSet.noneOf(ReferenceType.class);
         for (ReferenceType type : types) this.types.add(type);
    }

    private boolean collects(ReferenceType type) {
        return types.contains(type);
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
        if (collects(ReferenceType.ARRAY_ACCESS)) expressions.add(arrayAccess);
    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) {

    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLhs().accept(this);
        binaryExpression.getRhs().accept(this);
        if (binaryExpression instanceof AssignmentExpression ass && collects(ReferenceType.ASSIGNMENT_EXPRESSION))
            expressions.add(ass);
    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        if (this.visitInternal || !fieldAccess.getEncapsulated().isInternal())
            fieldAccess.getEncapsulated().accept(this);
        if (collects(ReferenceType.FIELD)) expressions.add(fieldAccess);
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
        if (this.visitInternal || !refObj.isInternal())
            refObj.accept(this);

        Arguments arguments = methodInvocation.getArguments();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
        }
        if (collects(ReferenceType.METHOD_INVOCATION)) expressions.add(methodInvocation);
    }

    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {
        negativeExpression.getEncapsulated().accept(this);
    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {
        if (collects(ReferenceType.NEW_ARRAY)) expressions.add(newArrayExpression);
        newArrayExpression.getNumElements().accept(this);
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {
        if (collects(ReferenceType.NEW_OBJECT)) expressions.add(newObjectExpression);
    }

    @Override
    public void visitNullValue(NullValue nullValue) {
        if (collects(ReferenceType.NULL_VALUE)) expressions.add(nullValue);
    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {

    }

    @Override
    public void visitThisValue(ThisValue thisValue) {
        if (collects(ReferenceType.THIS)) expressions.add(thisValue);
    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {
        if (collects(ReferenceType.UNINITIALIZED_VALUE)) expressions.add(uninitializedValue);
    }

    @Override
    public void visitVariable(Variable variable) {
        if (collects(ReferenceType.VARIABLE)) expressions.add(variable);
    }

    public void setVisitInternalNodes(boolean visitInternal) {
        this.visitInternal = visitInternal;
    }

}
