package de.dercompiler.ast.visitor;

import de.dercompiler.ast.statement.*;

public interface ASTStatementVisitor {
    void visitBasicBlock(BasicBlock basicBlock);
    void visitEmptyStatement(EmptyStatement emptyStatement);
    void visitErrorStatement(ErrorStatement errorStatement);
    void visitExpressionStatement(ExpressionStatement expressionStatement);
    void visitIfStatement(IfStatement ifStatement);
    void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement);
    void visitReturnStatement(ReturnStatement returnStatement);
    void visitWhileStatement(WhileStatement whileStatement);
}
