package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTLazyStatementVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.pass.*;

import java.util.Stack;

public class DeadCodeEliminationPass implements StatementPass, BasicBlockPass, ASTStatementVisitor {

    private Stack<Statement> resetStack;
    private boolean dead;
    private Method method;

    public Statement following(Statement statement) {
        final Statement[] sur = {statement.getSurroundingStatement()};
        final Statement[] res = new Statement[1];
        ASTStatementVisitor getNext = new ASTStatementVisitor() {
            @Override
            public void visitBasicBlock(BasicBlock basicBlock) {
                boolean found = false;
                res[0] = null;
                for (Statement s : basicBlock.getStatements()) {
                    if (found) {
                        res[0] = s;
                        break;
                    }
                    if (s == sur[0]) {
                        found = true;
                    }
                }
                if (basicBlock.getSurroundingStatement() != basicBlock) {
                    sur[0] = basicBlock;
                    basicBlock.getSurroundingStatement().accept(this);
                }
            }

            @Override
            public void visitEmptyStatement(EmptyStatement emptyStatement) {
                res[0] = null;
                //should never happen
            }

            @Override
            public void visitErrorStatement(ErrorStatement errorStatement) {
                res[0] = null;
                //should never happen
            }

            @Override
            public void visitExpressionStatement(ExpressionStatement expressionStatement) {
                res[0] = null;
                //should never happen
            }

            @Override
            public void visitIfStatement(IfStatement ifStatement) {
                if (ifStatement.hasElse() && ifStatement.getThenStatement() == sur[0]) {
                    res[0] = ifStatement.getElseStatement();
                } else {
                    sur[0] = ifStatement;
                    ifStatement.accept(this);
                }
            }

            @Override
            public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
                res[0] = null;
                //should never happen
            }

            @Override
            public void visitReturnStatement(ReturnStatement returnStatement) {
                res[0] = null;
                //should never happen
            }

            @Override
            public void visitWhileStatement(WhileStatement whileStatement) {
                sur[0] = whileStatement;
                whileStatement.accept(this);
            }
        };

        return res[0];
    }

    @Override
    public void doInitialization(Program program) {
        resetStack = new Stack<>();
        dead = false;
    }

    @Override
    public void doFinalization(Program program) {

    }

    private void updateMethod(BasicBlock block) {
        if (method != block.getSurroundingMethod()) {
            method = block.getSurroundingMethod();
            dead = false;
        }
    }

    private void otherFlow(Statement statement) {
        if (resetStack.peek() == statement) {
            resetStack.pop();
        }
        dead = false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        updateMethod(block);
        otherFlow(block);
        block.accept(this);
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        otherFlow(statement);
        statement.accept(this);
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(ASTReferencePass.class);
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
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

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {
        if (dead) {
            basicBlock.markDead();
        }
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
        if (dead) {
            emptyStatement.markDead();
        }
    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {
        if (dead) {
            errorStatement.markDead();
        }
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        if (dead) {
            expressionStatement.markDead();
        }
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        if (dead) {
            ifStatement.markDead();
        } else {
            if (ifStatement.hasElse()) {
                resetStack.push(ifStatement.getElseStatement());
            }
            resetStack.push(following(ifStatement));
        }
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
        if (dead) {
            localVariableDeclarationStatement.markDead();
        }
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {

        if (dead) {
            whileStatement.markDead();
        } else {
            resetStack.push(following(whileStatement));
        }
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        if (dead) {
            returnStatement.markDead();
        }
        dead = true;
    }
}
