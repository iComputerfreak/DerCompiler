package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;

public class CountVariablesPass implements MethodPass, StatementPass {

    int localVars = 0;

    @Override
    public void doInitialization(Program program) {
        localVars = 0;
    }

    @Override
    public void doFinalization(Program program) {
        assert(localVars == 0);
    }

    @Override
    public boolean shouldRunOnStatement(Statement statement) {
        return statement instanceof LocalVariableDeclarationStatement;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof LocalVariableDeclarationStatement lvds) {
            if (lvds.setNodeId(localVars)) localVars++;
        }
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {
        method.setNumLocalVariables(localVars);
        localVars = 0;
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(VariableAnalysisCheckPass.class);
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
        return AnalysisDirection.BOTTOM_UP;
    }
}
