package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.type.Type;
import de.dercompiler.pass.AnalysisDirection;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.*;

import java.util.HashMap;
import java.util.Set;

/**
 * hier werden für jede Klasse ihre öffentlichen Felder und methoden gesammelt
 */
public class InterClassAnalysisCheckPass implements ClassPass {
    //hier werden die Felder und Methoden in der Hashmap gespeichert. Der Key ist der Klassenname

    private HashMap<String, ClassDeclaration> classMap;

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        if (classMap.containsKey(classDeclaration.getIdentifier())){
            //Error, da Klasse mit gleichem Namen schon vorhanden
        }

        HashMap<String, Field> fieldMap = classDeclaration.getFieldMap();
        HashMap<String, Method> methodMap = classDeclaration.getMethodMap();

       for(ClassMember classMember: classDeclaration.getMembers()){
         if (classMember instanceof Method method){
             if (methodMap.containsKey(method.getIdentifier())){
                 //Error, da Methode mit gleichem Namen in der Klasse schon existiert
             }
             methodMap.put(method.getIdentifier(), method);
         } else if (classMember instanceof Field field){
            if (fieldMap.containsKey(field.getIdentifier())){
                //Error, da Feld mit gleichem Namen in der Klasse schon existiert
            }
            fieldMap.put(field.getIdentifier(), field);
         }
       }



       return false;
    }



    @Override
    public void doInitialization(Program program) {
        classMap = program.getClassMap();

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
