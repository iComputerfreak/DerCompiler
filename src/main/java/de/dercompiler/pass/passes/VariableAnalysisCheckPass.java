package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BooleanType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.*;
import de.dercompiler.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * hier werden für jede Klasse ihre Variablendeklarationen überprüft
 */
public class VariableAnalysisCheckPass implements ClassPass, MethodPass, StatementPass, ExpressionPass {

    private SymbolTable symbolTable;
    
    private StringTable stringTable;

    @Override
    public void doInitialization(Program program) {
        // Get the symbol table from the Program.
        // We only need a single SymbolTable for the whole analysis, since we can differentiate between
        // Symbols for variables and symbols for methods via the StringTables.
        this.symbolTable = program.getSymbolTable();
    }

    @Override
    public void doFinalization(Program program) {}
    
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        stringTable = new StringTable();

        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Field field) {
                insert(field.getIdentifier(), field, true);
            }
        }
        
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {

        for (Parameter parameter : method.getParameters()) {
            insert(parameter.getIdentifier(), parameter);
        }
        return false;
    }
    
    @Override
    public boolean runOnStatement(Statement statement) {

        // Insert variable
        if (statement instanceof LocalVariableDeclarationStatement s){
            insert(s.getIdentifier(), s);
        }
        
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        List<Variable> referencedVariables = Utils.getReferencedVariables(expression);

        for(Variable variable: referencedVariables){
            if (!stringTable.contains(variable.getName())){
                // TODO: Error, da referenzierte Variable nicht existiert
            }
            variable.setDefinition(stringTable.findOrInsert(variable.getName()).getCurrentDef());
        }
        return false;
    }

    private void insert(String identifier, ASTDefinition definition){
        insert(identifier, definition, false);
    }

    private void insert(String identifier, ASTDefinition definition, boolean inOutestScope){
        Symbol symbol = stringTable.findOrInsert(identifier);
        if (symbolTable.isDefinedInCurrentScope(symbol)){
            //Error, da identifier in diesem Scope schon definiert wurde
        }
        if (!inOutestScope && symbolTable.isDefinedInNotOutestScope(symbol)){
            //Error, da identifier schon definiert wurde und nicht im äußersten scope (klassenvariablen)
        }

        symbolTable.insert(symbol, definition);
    }


    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(EnterScopePass.class);
        usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
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
