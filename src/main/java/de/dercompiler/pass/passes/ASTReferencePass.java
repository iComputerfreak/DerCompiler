package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.pass.*;

import java.util.Objects;
import java.util.Stack;

/**
 *  (Pass 1) Sets references from many syntactic elements to their surrounding parent of the next higher syntactic category.
 */
public class ASTReferencePass implements ClassPass, MethodPass, StatementPass, BasicBlockPass, ExpressionPass, ASTStatementVisitor {

    private boolean shouldRun = false;
    private Stack<BasicBlock> blockStack;

    public ASTReferencePass() {}

    @Override
    public void doInitialization(Program program) {
        shouldRun = !program.isIndexed();
        blockStack = new Stack<>();
    }

    @Override
    public void doFinalization(Program program) {
        program.indexed();
    }

    @Override
    public boolean checkClass(BasicBlock block) {
        return shouldRun;
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return shouldRun;
    }

    @Override
    public boolean shouldRunOnMethod(Method method) {
        return shouldRun;
    }

    @Override
    public boolean shouldRunOnStatement(Statement statement) {
        return shouldRun;
    }

    @Override
    public boolean runOnMethod(Method method) {
        for (Parameter param : method.getParameters()) {
            param.setClassDeclaration(manager.getCurrentClass());
        }
        method.setSurroundingClass(manager.getCurrentClass());
        BasicBlock block = method.getBlock();
        block.setSurroundingStatement(block);
        blockStack.push(block);
        return false;
    }

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) { }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) { }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) { }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) { }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getThenStatement().setSurroundingStatement(ifStatement);
        if (ifStatement.hasElse()) {
            ifStatement.getElseStatement().setSurroundingStatement(ifStatement);
        }
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) { }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) { }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        whileStatement.getLoop().setSurroundingStatement(whileStatement);
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        block.setSurroundingMethod(manager.getCurrentMethod());
        blockStack.push(block);
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.setSurroundingMethod(manager.getCurrentMethod());
        if (Objects.isNull(statement.getSurroundingStatement())) {
            statement.setSurroundingStatement(blockStack.peek());
        }
        statement.accept(this);
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        expression.setSurroundingStatement(manager.getCurrentStatement());
        return false;
    }

    public void pop() {
        blockStack.pop();
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(InterClassAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        return AnalysisDirection.TOP_DOWN;
    }

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {

        for (ClassMember member : classDeclaration.getMembers()) {
            if (member instanceof Field f) {
                f.setClassDeclaration(classDeclaration);
            }
        }
        return false;
    }
}
