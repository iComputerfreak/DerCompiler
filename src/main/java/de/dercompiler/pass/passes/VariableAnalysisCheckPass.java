package de.dercompiler.pass.passes;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.ClassMember;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Program;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.*;

public class VariableAnalysisCheckPass implements ClassPass {
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        SymbolTable symbolTable = new SymbolTable();

        StringTable stringTable = new StringTable();

        for(ClassMember classMember: classDeclaration.getMembers()){
            if (classMember instanceof Field){
                Field field = (Field) classMember;

                Symbol symbol = stringTable.findOrInsert(field.getIdentifier());
                if (symbolTable.isDefinedInCurrentScope(symbol)){
                    //Error, da schon definiert
                }

                Definition definition = new FieldDefinition(symbol, field.getType());

                symbolTable.insert(symbol, definition);

            }
        }


        return false;
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
