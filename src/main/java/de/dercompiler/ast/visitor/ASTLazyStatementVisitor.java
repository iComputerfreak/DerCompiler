package de.dercompiler.ast.visitor;

import de.dercompiler.ast.statement.*;

public class ASTLazyStatementVisitor implements ASTStatementVisitor {

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {
        // do nothing.
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
// do nothing.
    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {
// do nothing.
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
// do nothing.
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
// do nothing.
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
// do nothing.
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
// do nothing.
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
// do nothing.
    }
}
