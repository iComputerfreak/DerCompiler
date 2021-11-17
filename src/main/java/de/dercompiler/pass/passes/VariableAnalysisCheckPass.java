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
public class VariableAnalysisCheckPass implements ClassPass, MethodPass, StatementPass, BasicBlockPass, ExpressionPass {

    private SymbolTable symbolTable;
    
    /*
    @Thomas
     - Du musst nie enterScope() oder leaveScope() aufrufen. Das macht ein extra Pass direkt vor und nach den runOn... Aufrufen.
     - Du brauchst nur eine StringTable pro Scope, da die StringTable eine extra HashMap für Klassen, Methoden und Variables hat. (Siehe insert())
     */

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
        StringTable stringTable = new StringTable();
        // TODO: Insert class name into symbol table
        
        // NOTE: This is the only case besides Parameter where we "step down" into the properties of a node we run on.
        // Ideally, this should run in a separate function, like "runOnField(Field field)", which is not implemented yet.
        // So we check the fields here in runOnClass for now.
        // Insert fields
        for (ClassMember classMember : classDeclaration.getMembers()) {
            if (classMember instanceof Field field) {
                insert(field.getIdentifier(), field.getType(), symbolTable, stringTable, true);
            }
        }
        
        return false;
    }

    @Override
    public boolean runOnMethod(Method method) {
        // TODO: Where to get/store string table?
        StringTable stringTable = null;
        // Insert method name
        // TODO: Insert method name into symbol table
        // Maybe like this?
        // insert(method.getIdentifier(), method.getType(), symbolTable, stringTable, true);
        
        // Insert parameters
        for (Parameter parameter : method.getParameters()) {
            insert(parameter.getIdentifier(), parameter.getType(), symbolTable, stringTable);
        }
        return false;
    }
    
    @Override
    public boolean runOnStatement(Statement statement) {
        // TODO: Where to get/store string table?
        StringTable stringTable = null;
        
        // Insert variable
        if (statement instanceof LocalVariableDeclarationStatement s){
            insert(s.getIdentifier(), s.getType(), symbolTable, stringTable);
        }
        
        return false;
    }
    
    @Override
    public boolean runOnBasicBlock(BasicBlock basicBlock) {
        // We don't have to do anything for BasicBlocks themselves, just for the contents
        // (see runOnExpression and runOnStatement)
        // @Thomas wenn du willst kannst du runOnBasicBlock auch löschen und oben das BasicBlockPass weg machen
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        // TODO: Where to get/store string table?
        StringTable stringTable = null;
        
        List<Variable> referencedVariables = Utils.getReferencedVariables(expression);

        for(Variable variable: referencedVariables){
            if (!stringTable.containsVariable(variable.getName())){
                // TODO: Error, da referenzierte Variable nicht existiert
            }
        }
        return false;
    }

    // TODO: insert needs to know what it is inserting, i.e. is it inserting a variable, method or class
    private void insert(String identifier, Type type, SymbolTable symbolTable, StringTable stringTable){
        insert(identifier, type, symbolTable, stringTable, false);
    }

    private void insert(String identifier, Type type, SymbolTable symbolTable, StringTable stringTable, boolean inOutestScope){
        // TODO: Use findOrInsertClass, findOrInsertMethod or findOrInsertVariable 
        Symbol symbol = null; // stringTable.findOrInsert(identifier);
        if (symbolTable.isDefinedInCurrentScope(symbol)){
            //Error, da identifier in diesem Scope schon definiert wurde
        }
        if (!inOutestScope && symbolTable.isDefinedInNotOutestScope(symbol)){
            //Error, da identifier schon definiert wurde und nicht im äußersten scope (klassenvariablen)
        }
        Definition definition = new FieldDefinition(symbol, type);
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
