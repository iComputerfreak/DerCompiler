package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.pass.AnalysisDirection;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.MethodPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import firm.CompoundType;
import firm.Construction;
import firm.Entity;
import firm.Graph;

public class FirmMethodgraphPass implements MethodPass {
    private GlobalScope globalScope;

    @Override
    public boolean runOnMethod(Method method) {
        MethodDefinition def = globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        //wie bekommt man den globalType??
        CompoundType globalType = null;
        Entity methodEntity = new Entity(globalType, method.getIdentifier(), def.getFirmType());
        int n_vars = 42;
        Graph graph = new Graph(methodEntity, n_vars);
        Construction construction = new Construction(graph);

        return false;
    }

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
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
    public PassManager getPassManager() {
        return null;
    }

    @Override
    public long registerID(long id) {
        return 0;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public AnalysisDirection getAnalysisDirection() {
        return null;
    }
}
