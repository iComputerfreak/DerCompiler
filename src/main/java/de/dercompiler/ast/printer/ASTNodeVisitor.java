package de.dercompiler.ast.printer;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;

public interface ASTNodeVisitor {

    void visitArguments(Arguments arguments);
    void visitArrayAccess(ArrayAccess arrayAccess);
    void visitBasicBlock(BasicBlock basicBlock);
    void visitBasicType(BasicType basicType);
    void visitBinaryExpression(BinaryExpression binaryExpression);
    void visitClassDeclaration(ClassDeclaration classDeclaration);
    void visitEmptyStatement(EmptyStatement emptyStatement);
    void visitErrorClassMember(ErrorClassMember errorClassMember);
    void visitErrorExpression(ErrorExpression errorExpression);
    void visitErrorStatement(ErrorStatement errorStatement);
    void visitExpressionStatement(ExpressionStatement expressionStatement);
    void visitField(Field field);
    void visitFieldAccess(FieldAccess fieldAccess);
    void visitIfStatement(IfStatement ifStatement);
    void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement);
    void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression);
    void visitMainMethod(MainMethod mainMethod);
    void visitMethod(Method method);
    void visitMethodInvocation(MethodInvocationOnObject methodInvocation);
    void visitMethodRest(MethodRest methodRest);
    void visitNegativeExpression(NegativeExpression negativeExpression);
    void visitNewArrayExpression(NewArrayExpression newArrayExpression);
    void visitNewObjectExpression(NewObjectExpression newObjectExpression);
    default void visitNode(ASTNode node) {
        node.accept(this);
    }
    void visitParameter(Parameter parameter);
    void visitPrimaryExpression(PrimaryExpression primaryExpression);
    void visitProgram(Program program);
    void visitReturnStatement(ReturnStatement returnStatement);

    void visitType(Type type);
    void visitUnitializedValue(UninitializedValue uninitializedValue);
    void visitVoidExpression(VoidExpression voidExpression);
    void visitWhileStatement(WhileStatement whileStatement);

}
