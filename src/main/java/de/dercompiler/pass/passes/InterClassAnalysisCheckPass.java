package de.dercompiler.pass.passes;

import de.dercompiler.Program;
import de.dercompiler.ast.*;
import de.dercompiler.ast.type.VoidType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.*;

/**
 *  (Pass 2) Collects all fields and methods of all classes.
 */
public class InterClassAnalysisCheckPass implements ClassPass {


    private GlobalScope globalScope;
    private OutputMessageHandler logger;


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        String className = classDeclaration.getIdentifier();
        if (globalScope.hasClass(className) && !(globalScope.getClass(className) instanceof InternalClass || globalScope.getClass(className) instanceof DummyClassType)) {
            failAnalysis(PassErrorIds.DUPLICATE_CLASS, classDeclaration, "Class definition of %s may not be overridden.".formatted(className));
        }

        ClassType newClass = new ClassType(className);
        newClass.setDecl(classDeclaration);

        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Method method) {
                if (newClass.hasMethod(method.getIdentifier())) {
                    failAnalysis(PassErrorIds.DUPLICATE_METHOD, method, "Method %s is already defined in class %s".formatted(method.getIdentifier(), newClass.getIdentifier()));
                }
                MethodDefinition methodDefinition = new MethodDefinition(method.getIdentifier(), newClass);
                methodDefinition.setMethod(method);

                newClass.addMethod(methodDefinition);
            } else if (classMember instanceof Field field) {
                if (field.getType().getBasicType() instanceof VoidType) {
                    failAnalysis(PassErrorIds.ILLEGAL_FIELD_TYPE, field, "Illegal type %s for attribute %s".formatted(field.getType(), field.getIdentifier()));
                }
                if (newClass.hasField(field.getIdentifier())) {
                    failAnalysis(PassErrorIds.DUPLICATE_FIELD, field, "Field %s is already defined in class %s".formatted(field.getIdentifier(), newClass.getIdentifier()));
                }
                FieldDefinition fieldDefinition = new FieldDefinition(field.getIdentifier(), newClass);
                fieldDefinition.setNode(field);
                newClass.addField(fieldDefinition);
            }
        }

        validateDummy(newClass);
        globalScope.addClass(newClass);

        return false;
    }

    private void validateDummy(ClassType newClass) {
        String className = newClass.getIdentifier();
        if (!globalScope.hasClass(className) || globalScope.getClass(className) instanceof InternalClass) {
            return;
        }

        ClassType duplicate = globalScope.getClass(className);

        DummyClassType dummyType = (DummyClassType) duplicate;
        for (FieldDefinition f : dummyType.getFields()) {
            String fieldName = f.getIdentifier();
            if (!newClass.hasField(fieldName)) {
                failAnalysis(PassErrorIds.UNKNOWN_FIELD, null, "Field '%s' of type %s is not defined".formatted(fieldName, className));
            } else {
                Type fieldType = newClass.getField(fieldName).getType();
                if (fieldType.isCompatibleTo(dummyType.getField(fieldName).getType())) {
                    failAnalysis(PassErrorIds.UNKNOWN_FIELD, null, "Field '%s' of type %s was supposed to be type %s".formatted(fieldName, className, fieldType));
                }
            }
        }
        for (MethodDefinition m : dummyType.getMethods()) {
            String methodName = m.getIdentifier();
            if (!newClass.hasMethod(methodName)) {
                failAnalysis(PassErrorIds.UNKNOWN_METHOD, null, "Method '%s' of type %s is not defined".formatted(methodName, className));
            }
        }

        // From now on, dummy acts as a proxy of the real type.
        dummyType.setRealType(newClass);
    }

    private void failAnalysis(PassErrorIds errorId, ASTNode node, String message) {
        if (node != null) getPassManager().getLexer().printSourceText(node.getSourcePosition());
        logger.printErrorAndExit(errorId, message);
        getPassManager().quitOnError();
    }

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
        logger = new OutputMessageHandler(MessageOrigin.PASSES);
        TypeFactory.getInstance().setCreateDummies(true);
    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
       // usage.requireAnalysis(ASTReferencePass.class);
       // usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        // direction does not matter here
        return AnalysisDirection.TOP_DOWN;
    }
}
