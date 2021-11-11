package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;

public class ASTReferencePass implements MethodPass, StatementPass, BasicBlockPass,  ExpressionPass {

    private boolean shouldRun = false;

    public ASTReferencePass() {}

    @Override
    public void doInitialization(Program program) {
        shouldRun = !program.isIndexed();
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
    public boolean checkExpression(Expression expression) {
        return shouldRun;
    }

    @Override
    public boolean checkMethod(Method method) {
        return shouldRun;
    }

    @Override
    public boolean checkStatement(Statement statement) {
        return shouldRun;
    }

    @Override
    public boolean runOnMethod(Method method) {
        method.setSurroundingClass(manager.getCurrentClass());
        return false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        block.setSurroundingMethod(manager.getCurrentMethod());
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.setSurroundingMethod(manager.getCurrentMethod());
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        expression.setSurroundingStatement(manager.getCurrentStatement());
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
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
}
