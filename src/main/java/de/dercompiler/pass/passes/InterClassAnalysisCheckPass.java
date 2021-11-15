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
    //hier werden die Felder und Methoden in der Hashmap gespeichert. Der Key ist der Klassenname
    HashMap<String, StringTable> methodStringTables, fieldStringTables;
    HashMap<String, SymbolTable> methodSymbolTables, fieldSymbolTables;


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
       classDeclaration.setFieldStringTables(fieldStringTables);
       classDeclaration.setFieldSymbolTables(fieldSymbolTables);
       classDeclaration.setMethodStringTables(methodStringTables);
       classDeclaration.setMethodSymbolTables(methodSymbolTables);

       StringTable methodStringTable = new StringTable();
       SymbolTable methodSymbolTable = new SymbolTable();
       StringTable fieldStringTable = new StringTable();
       SymbolTable fieldSymbolTable = new SymbolTable();

       methodStringTables.put(classDeclaration.getIdentifier(), methodStringTable);
       methodSymbolTables.put(classDeclaration.getIdentifier(), methodSymbolTable);
       fieldStringTables.put(classDeclaration.getIdentifier(), fieldStringTable);
       fieldSymbolTables.put(classDeclaration.getIdentifier(), fieldSymbolTable);


       for(ClassMember classMember: classDeclaration.getMembers()){
         if (classMember instanceof Method method){
             insert(method.getIdentifier(), method.getType(),methodSymbolTable, methodStringTable);
         } else if (classMember instanceof Field field){
             insert(field.getIdentifier(), field.getType(), fieldSymbolTable, fieldStringTable);
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
        methodStringTables = new HashMap<String, StringTable>();
        fieldStringTables = new HashMap<String, StringTable>();
        methodSymbolTables = new HashMap<String, SymbolTable>();
        fieldSymbolTables = new HashMap<String, SymbolTable>();

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
