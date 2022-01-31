package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.Program;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;

public class CountVariablesPass implements MethodPass, StatementPass {

    int localVars;
    Method method;
    int parameterCount;

    @Override
    public void doInitialization(Program program) {
        localVars = 0;
        method = null;
        parameterCount = 0;
    }

    @Override
    public void doFinalization(Program program) {
        assert(localVars == 0);
    }

    @Override
    public boolean shouldRunOnStatement(Statement statement) {
        return statement instanceof LocalVariableDeclarationStatement;
    }

    private void updateMethod() {
        if (method == getPassManager().getCurrentMethod()) return;
        method = getPassManager().getCurrentMethod();
        assert(localVars == 0);
        parameterCount = method.getParameters().size();
        localVars = parameterCount + (method.isStatic() ? 0 : 1); //set localVars start-index to parameterCount, so we can use the lover localVars for parameters
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        updateMethod();
        if (statement instanceof LocalVariableDeclarationStatement lvds) {
            if (!lvds.isIdSet()) {
                lvds.setNodeId(localVars);
                localVars++;
            }
        }
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {
        // if this method contains no local variable declarations, this is necessary:
        updateMethod();
        int i = 0;
        int base = 0;
        if (!method.isStatic()) {
            i = 1;
            base = 1;
        }
        for (Parameter param : method.getParameters()) {
            if (!param.isIdSet()) {
                param.setNodeId(i);
                i++;
            }
        }
        if (i != base + parameterCount) new OutputMessageHandler(MessageOrigin.PASSES)
                .internalError("something gone wrong, we can't count, the number of parameters is: " + parameterCount + ", but we counted until: " + i + " for method: " + method.getIdentifier() + "!");
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
