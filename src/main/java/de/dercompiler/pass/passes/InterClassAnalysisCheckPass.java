package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.type.Type;
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
    StringTable fieldStringTable, methodStringtable, classStringTable;
    SymbolTable fieldSymbolTable, methodSymbolTable;


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        if (classStringTable.contains(classDeclaration.getIdentifier())){
            //Error, da Klasse schon vorhanden
        }
        classStringTable.findOrInsert(classDeclaration.getIdentifier());


        for(ClassMember classMember: classDeclaration.getMembers()){
            if (classMember instanceof Field){
                Field field = (Field) classMember;

                insert(field.getIdentifier(), field.getType(), fieldSymbolTable, fieldStringTable);
            }

            if (classMember instanceof Method){
                Method method = (Method) classMember;

                insert(method.getIdentifier(), method.getType(), methodSymbolTable, methodStringtable);
            }
        }



        return false;
    }

    private void insert(String identifier, Type type, SymbolTable symbolTable, StringTable stringTable){
        Symbol symbol = stringTable.findOrInsert(identifier);
        if (symbolTable.isDefinedInCurrentScope(symbol)){
            //Error, da identifier in diesem Scope schon definiert wurde
        }
        Definition definition = new FieldDefinition(symbol, type);
        symbolTable.insert(symbol, definition);
    }

    @Override
    public void doInitialization(Program program) {
        classStringTable = new StringTable();
        fieldStringTable = new StringTable();
        methodStringtable = new StringTable();
        fieldSymbolTable = new SymbolTable();
        methodSymbolTable = new SymbolTable();
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
    public long registerID(long id) {
        return 0;
    }

    @Override
    public long getID() {
        return 0;
    }
}
