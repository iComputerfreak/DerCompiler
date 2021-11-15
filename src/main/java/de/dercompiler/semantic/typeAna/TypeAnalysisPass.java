package de.dercompiler.semantic.typeAna;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.pass.*;

public class TypeAnalysisPass implements MethodPass, ExpressionPass {
    @Override
    public boolean runOnExpression(Expression expression) {
        return false;
    }

    @Override
    public boolean checkExpression(Expression expression) {
        return ExpressionPass.super.checkExpression(expression);
    }

    @Override
    public boolean runOnMethod(Method method) {
        return false;
    }

    @Override
    public boolean checkMethod(Method method) {
        return MethodPass.super.checkMethod(method);
    }

    @Override
    public PassDependencyType getMinDependencyType() {
        return MethodPass.super.getMinDependencyType();
    }

    @Override
    public PassDependencyType getMaxDependencyType() {
        return MethodPass.super.getMaxDependencyType();
    }

    @Override
    public void doInitialization(Program program) {

    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        return null;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return null;
    }

    @Override
    public void registerPassManager(PassManager manager) {

    }

    @Override
    public long registerID(long id) {
        return 0;
    }

    @Override
    public long getID() {
        return 0;
    }
}
