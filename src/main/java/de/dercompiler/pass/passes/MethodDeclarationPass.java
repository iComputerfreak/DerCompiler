package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.MethodType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.semantic.type.TypeFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MethodDeclarationPass implements MethodPass {

    private PassManager passManager;
    private GlobalScope globalScope;

    @Override
    public boolean runOnMethod(Method method) {
        TypeFactory typeFactory = TypeFactory.getInstance();
        Type returnType = typeFactory.create(method.getType());
        List<Type> parameterTypes = method.getParameters().stream()
                .map(p -> typeFactory.create(p.getType()))
                .collect(Collectors.toList());
        method.setReferenceType(new MethodType(returnType, parameterTypes));

        return false;
    }

    @Override
    public void doInitialization(Program program) {
        this.globalScope = program.getGlobalScope();
    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(InterClassAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return null;
    }

    @Override
    public void registerPassManager(PassManager manager) {
        this.passManager = manager;
    }

    @Override
    public PassManager getPassManager() {
        return this.passManager;
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
        return AnalysisDirection.TOP_DOWN;
    }
}
