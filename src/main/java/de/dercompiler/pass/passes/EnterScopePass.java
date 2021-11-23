package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.SymbolTable;

/**
 *  (Pass 4) Enters a scope for each class, method and basic block.
 */
public class EnterScopePass implements ClassPass, MethodPass, BasicBlockPass {
    
    private SymbolTable symbolTable;
    
    @Override
    public void doInitialization(Program program) {
        symbolTable = program.getSymbolTable();
        symbolTable.enterScope();
    }

    @Override
    public void doFinalization(Program program) {}

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        symbolTable.enterScope();
        return false;
    }

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        symbolTable.enterScope();
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {
        symbolTable.enterScope();
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(MemberDeclarationPass.class);
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
        return AnalysisDirection.TOP_DOWN;
    }
}
