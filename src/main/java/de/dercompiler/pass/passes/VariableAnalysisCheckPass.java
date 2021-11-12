package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.Type;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.*;

public class VariableAnalysisCheckPass implements ClassPass {
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.enterScope();

        StringTable stringTable = new StringTable();

        //hier werden erst die Feldernamen gesammelt
        for(ClassMember classMember: classDeclaration.getMembers()){
            if (classMember instanceof Field){
                Field field = (Field) classMember;

                insert(field.getIdentifier(), field.getType(), symbolTable, stringTable);
            }
        }

        //jetzt werden in die Methoden gesprungen
        for(ClassMember classMember: classDeclaration.getMembers()){
            symbolTable.enterScope();

            if (classMember instanceof Method){

                Method method = (Method) classMember;

                for (Parameter parameter: method.getParameters()){
                    insert(parameter.getIdentifier(), parameter.getType(), symbolTable, stringTable);
                }

                for (Statement statement: method.getBlock().getStatements()){
                    visitStatement(statement, symbolTable, stringTable);
                }



            }
        }


        return false;
    }

    private void visitStatement(Statement statement, SymbolTable symbolTable, StringTable stringTable){
        if (statement instanceof BasicBlock){

        }

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
