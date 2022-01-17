package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;

import java.util.Stack;


public class DeadCodeEliminationPass implements BasicBlockPass, ASTStatementVisitor {

    private boolean dead;
    private Stack<Boolean> state;
    private Class<? extends Pass> pass;
    private DependencyType dep;

    public Pass init(Class<? extends Pass> pass, DependencyType dep) {
        this.pass = pass;
        this.dep = dep;
        return this;
    }

    @Override
    public void doInitialization(Program program) {
        dead = false;
        state = new Stack<>();
        state.push(false);
    }

    @Override
    public void doFinalization(Program program) {

    }

    private void foundDead() {
        dead = true;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        state.push(dead);
        dead = false;
        for (Statement s : block.getStatements()) {
            if (dead) {
                s.markDead();
            } else {
                s.accept(this);
            }
        }
        if (dead) {
            block.returns();
        }
        dead = state.pop();
        return false;
    }


    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {
        if (basicBlock.hasReturn()) {
            foundDead();
        }
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
        //do nothing
    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {
        //do nothing
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        //do nothing
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        if (!ifStatement.hasElse()) return;
        boolean bothReturn = ifStatement.getThenStatement().hasReturn() && ifStatement.getElseStatement().hasReturn();
        if (bothReturn) {
            foundDead();
            ifStatement.returns();
        }
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
        //do nothing
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        if (whileStatement.getLoop().hasReturn()); //while is if
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        foundDead();
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        if (pass != null && dep != null) {
            usage.requireAnalysis(pass);
            usage.setDependency(dep);
        }
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

    private static long id = 0;
    private PassManager manager = null;

    @Override
    public void registerPassManager(PassManager manager) {
        this.manager = manager;
    }

    @Override
    public PassManager getPassManager() {
        return manager;
    }

    @Override
    public long registerID(long rid) {
        if (id != 0) return id;
        id = rid;
        return id;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public AnalysisDirection getAnalysisDirection() {
        return AnalysisDirection.BOTTOM_UP;
    }
}
