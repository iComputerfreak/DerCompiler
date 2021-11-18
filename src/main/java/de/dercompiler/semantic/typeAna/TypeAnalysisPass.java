package de.dercompiler.semantic.typeAna;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.pass.*;
import de.dercompiler.pass.passes.VariableAnalysisCheckPass;

public class TypeAnalysisPass implements MethodPass, ExpressionPass {
    
    @Override
    public void doInitialization(Program program) {

    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public boolean runOnExpression(Expression expression) {
        return false;
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return ExpressionPass.super.shouldRunOnExpression(expression);
    }

    @Override
    public boolean runOnMethod(Method method) {
        return false;
    }

    @Override
    public boolean shouldRunOnMethod(Method method) {
        return MethodPass.super.shouldRunOnMethod(method);
    }


    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(VariableAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
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
        // TODO: Change direction?
        return AnalysisDirection.TOP_DOWN;
    }
}
