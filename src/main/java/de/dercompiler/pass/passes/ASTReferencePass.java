package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;

public class ASTReferencePass implements MethodPass, StatementPass, ExpressionPass {

    private static long id = 0;
    PassManager manager = null;

    public ASTReferencePass() {}

    @Override
    public void doInitialization(Program program) {

    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public boolean runOnMethod(Method method) {
        method.setSurroundingClass(manager.getCurrentClass());
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
    public PassDependencyType getMinDependencyType() {
        return PassDependencyType.METHOD_PASS;
    }

    @Override
    public PassDependencyType getMaxDependencyType() {
        return PassDependencyType.EXPRESSION_PASS;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

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
}
