package de.dercompiler.pass.passes;

import de.dercompiler.Program;
import de.dercompiler.ast.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *  (Pass 3) Sets the types of method definitions and fields.
 */
public class MemberDeclarationPass implements ClassPass {

    private PassManager passManager;
    private GlobalScope globalScope;
    private TypeFactory typeFactory;

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        for (ClassMember member : classDeclaration.getMembers()) {
            if (member instanceof Field f) {
                runOnField(f);
            } else if (member instanceof Method m) {
                runOnMethod(m);
            }
        }
        return false;
    }

    public boolean runOnField(Field field) {
        ClassType tRefObj = globalScope.getClass(getPassManager().getCurrentClass().getIdentifier());
        FieldDefinition fieldDef = tRefObj.getField(field.getIdentifier());
        Type fieldType = typeFactory.create(field.getType());
        fieldDef.setType(fieldType);
        return false;
    }

    public boolean runOnMethod(Method method) {

        Type returnType = typeFactory.create(method.getType());
        List<Type> parameterTypes = method.getParameters().stream()
                .map(p -> typeFactory.create(p.getType()))
                .collect(Collectors.toList());

        int faultyTypeParamIdx = IntStream.range(0, parameterTypes.size())
                .filter(idx -> parameterTypes.get(idx) instanceof InternalClass)
                .findAny().orElse(-1);
        if (faultyTypeParamIdx >= 0) {
            new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.ILLEGAL_PARAMETER_TYPE,
                    "Illegal type %s for a method parameter".formatted(parameterTypes.get(faultyTypeParamIdx)));
        }
        ClassType tRefObj = globalScope.getClass(method.getSurroundingClass().getIdentifier());
        MethodType methodType = new MethodType(returnType, parameterTypes, method.isStatic());
        tRefObj.getMethod(method.getIdentifier()).setType(methodType);

        return false;
    }

    @Override
    public void doInitialization(Program program) {
        this.globalScope = program.getGlobalScope();
        this.typeFactory = TypeFactory.getInstance();
    }

    @Override
    public void doFinalization(Program program) {
        typeFactory.setCreateDummies(false);
        DummyClassType leftoverDummy = (DummyClassType) globalScope.getClasses().stream().filter(c -> c instanceof DummyClassType).findAny().orElse(null);
        if (leftoverDummy != null) {
            fail(PassErrorIds.UNKNOWN_TYPE, "Type '%s' was referenced but never defined.".formatted(leftoverDummy.getIdentifier()));
        }
    }

    private void fail(PassErrorIds errorId, String message) {
        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(errorId, message);
        getPassManager().quitOnError();
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        // First, ClassTypes need to be known, only then can MethodTyps and FieldTypes be assigned.
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
        return AnalysisDirection.BOTTOM_UP;
    }

}
