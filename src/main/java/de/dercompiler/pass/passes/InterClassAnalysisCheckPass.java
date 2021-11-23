package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.type.VoidType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.InternalClass;
import de.dercompiler.semantic.type.MethodType;

/**
 * Collects all fields and methods of all classes.
 */
public class InterClassAnalysisCheckPass implements ClassPass {


    private GlobalScope globalScope;
    private OutputMessageHandler logger;


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        String className = classDeclaration.getIdentifier();
        if (globalScope.hasClass(className) && !(globalScope.getClass(className) instanceof InternalClass)) {
            logger.printErrorAndExit(PassErrorIds.DUPLICATE_CLASS, "Class definition of %s may not be overridden.".formatted(className));
        }

        ClassType newClass = new ClassType(className);
        newClass.setDecl(classDeclaration);

        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Method method) {
                if (newClass.hasMethod(method.getIdentifier())) {
                    failAnalysis(PassErrorIds.DUPLICATE_METHOD, method, "Method %s is already defined in class %s".formatted(method.getIdentifier(), newClass.getIdentifier()));
                }
                MethodDefinition methodDefinition = new MethodDefinition(method.getIdentifier(), newClass);
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

        globalScope.addClass(newClass);

        return false;
    }

    private void failAnalysis(PassErrorIds errorId, ASTNode node, String message) {
        getPassManager().getLexer().printSourceText(node.getSourcePosition());
        logger.printErrorAndExit(errorId, message);
        getPassManager().quitOnError();
    }

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
         logger = new OutputMessageHandler(MessageOrigin.PASSES);
    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(ASTReferencePass.class);
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
        // direction does not matter here
        return AnalysisDirection.BOTTOM_UP;
    }
}
