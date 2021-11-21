package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.SymbolTable;

public class LeaveScopePass implements ClassPass, MethodPass, BasicBlockPass {

    private SymbolTable symbolTable;
    
    @Override
    public void doInitialization(Program program) {
        symbolTable = program.getSymbolTable();
    }

    @Override
    public void doFinalization(Program program) {
        symbolTable.leaveScope();
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        symbolTable.leaveScope();
        return false;
    }

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        symbolTable.leaveScope();
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {
        symbolTable.leaveScope();
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(TypeAnalysisPass.class);
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
