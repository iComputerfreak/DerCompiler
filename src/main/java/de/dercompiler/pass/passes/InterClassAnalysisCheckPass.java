package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.type.VoidType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.LibraryClass;

import java.util.HashMap;

/**
 * Collects all fields and methods of all classes.
 */
public class InterClassAnalysisCheckPass implements ClassPass {


    private GlobalScope globalScope;
    private OutputMessageHandler logger;


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        String className = classDeclaration.getIdentifier();
        if (globalScope.hasClass(className) && !(globalScope.getClass(className) instanceof LibraryClass)) {
            logger.printErrorAndExit(PassErrorIds.DUPLICATE_CLASS, "Class definition of %s may not be overridden.".formatted(className));
        }

        ClassType newClass = new ClassType(className);
        newClass.setDecl(classDeclaration);

        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Method method) {
                if (newClass.hasMethod(method.getIdentifier())) {
                    failAnalysis(PassErrorIds.DUPLICATE_METHOD, method, "Method %s is already defined in class %s".formatted(method.getIdentifier(), newClass.getIdentifier()));
                }
                newClass.addMethod(method.getIdentifier(), method);
            } else if (classMember instanceof Field field) {
                if (field.getType().getBasicType() instanceof VoidType) {
                    failAnalysis(PassErrorIds.ILLEGAL_FIELD_TYPE, field, "Illegal type %s for attribute %s".formatted(field.getType(), field.getIdentifier()));
                }
                if (newClass.hasField(field.getIdentifier())) {
                    failAnalysis(PassErrorIds.DUPLICATE_FIELD, field, "Field %s is already defined in class %s".formatted(field.getIdentifier(), newClass.getIdentifier()));
                }
                newClass.addField(field.getIdentifier(), field);
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
