package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;
import de.dercompiler.transformation.TransformUtil;

/**
 * Sets the firm type(s) of every class, field, method and local variable
 */
public class FirmTypePass implements ClassPass, ExpressionPass, MethodPass, StatementPass {
    
    @Override
    public void doInitialization(Program program) {}
    
    @Override
    public void doFinalization(Program program) {}
    
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        // TODO: Implement
        return false;
    }
    
    @Override
    public boolean runOnExpression(Expression expression) {
        return false;
    }
    
    @Override
    public boolean runOnMethod(Method method) {
        return false;
    }
    
    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof LocalVariableDeclarationStatement s) {
            // TODO: Set firm type
        }
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
        return null;
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
}
