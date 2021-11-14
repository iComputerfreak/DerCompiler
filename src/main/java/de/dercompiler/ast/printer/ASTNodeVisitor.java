package de.dercompiler.ast.printer;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;

public interface ASTNodeVisitor extends ASTExpressionVisitor {

    void visitArguments(Arguments arguments);
    void visitBasicBlock(BasicBlock basicBlock);
    void visitBasicType(BasicType basicType);
    void visitClassDeclaration(ClassDeclaration classDeclaration);
    void visitEmptyStatement(EmptyStatement emptyStatement);
    void visitErrorClassMember(ErrorClassMember errorClassMember);
    void visitErrorStatement(ErrorStatement errorStatement);
    void visitExpressionStatement(ExpressionStatement expressionStatement);
    void visitIfStatement(IfStatement ifStatement);
    void visitField(Field field);
    void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement);
    void visitMainMethod(MainMethod mainMethod);
    void visitMethod(Method method);
    void visitMethodRest(MethodRest methodRest);
    default void visitNode(ASTNode node) {
        node.accept(this);
    }
    void visitParameter(Parameter parameter);
    void visitProgram(Program program);

    void visitReturnStatement(ReturnStatement returnStatement);
    void visitType(Type type);
    void visitUninitializedValue(UninitializedValue uninitializedValue);
    void visitWhileStatement(WhileStatement whileStatement);

}
