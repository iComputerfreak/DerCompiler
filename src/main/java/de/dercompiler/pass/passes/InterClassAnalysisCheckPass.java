package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.pass.AnalysisDirection;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.ClassType;

import java.util.HashMap;

/**
 * hier werden für jede Klasse ihre öffentlichen Felder und methoden gesammelt
 */
public class InterClassAnalysisCheckPass implements ClassPass {


    private GlobalScope globalScope;

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        if (globalScope.hasClass(classDeclaration.getIdentifier())) {
            //Error, da Klasse mit gleichem Namen schon vorhanden
        }

        ClassType newClass = new ClassType(classDeclaration.getIdentifier());

        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Method method) {
                if (newClass.hasMethod(method.getIdentifier())) {
                    //Error, da Methode mit gleichem Namen in der Klasse schon existiert
                }
                newClass.addMethod(method.getIdentifier(), method);
            } else if (classMember instanceof Field field) {
                if (newClass.hasField(field.getIdentifier())) {
                    //Error, da Feld mit gleichem Namen in der Klasse schon existiert
                }
                newClass.addField(field.getIdentifier(), field);
            }
        }

        globalScope.addClass(newClass);

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
