package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import firm.*;

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

        //Graph als .vcg datei erzeugen
        Dump.dumpGraph(graph, method.getSurroundingClass().getIdentifier() +  "#" + method.getIdentifier());
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
        usage.requireAnalysis(FirmTypePass.class);
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
