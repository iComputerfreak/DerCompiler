package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        int faultyTypeParamIdx = IntStream.range(0, parameterTypes.size())
                .filter(idx -> parameterTypes.get(idx) instanceof LibraryClass)
                .findAny().orElse(-1);
        if (faultyTypeParamIdx >= 0) {
            new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.ILLEGAL_PARAMETER_TYPE,
                    "Illegal type %s for a method parameter".formatted(parameterTypes.get(faultyTypeParamIdx)));
        }
        method.setReferenceType(new MethodType(returnType, parameterTypes, method.isStatic()));

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
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
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
