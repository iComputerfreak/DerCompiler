package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.StringTable;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.Symbol;
import de.dercompiler.semantic.SymbolTable;
import de.dercompiler.semantic.VariableDefinition;

import java.util.LinkedList;
import java.util.List;

public class NameAnalysisCheckPass implements MethodPass, StatementPass, ExpressionPass {

    private static long id = 0;
    PassManager manager = null;
    private final StringTable stringTable = StringTable.getInstance();

    public NameAnalysisCheckPass() {}

    @Override
    public void doInitialization(Program program) {
        
    }

    @Override
    public void doFinalization(Program program) {
        
    }

    /**
     * Checks, if the given method is semantically valid in the current context
     * @param method The Method to check
     * @return Whether this run made any changes that would require other passes to run again
     */
    @Override
    public boolean runOnMethod(Method method) {
        // TODO: Get SymbolTable for current context
        SymbolTable symbolTable = null;
        
        // TODO: How do I specifically request the a method with this name?
        Symbol symbol = stringTable.findOrInsertMethod(method.getIdentifier());
        // If there currently is no method with this name, stringTable.getSymbol() will return a new Symbol
        // with a null Scope and Definition, which will fail the following if statement.
        // If the method is defined in a scope above the current scope, that is okay and no conflict.
        if (symbolTable.isDefinedInCurrentScope(symbol)) {
            // There already is a method with this name in the current scope
            // TODO: Error: Already defined
            return false;
        }
        
        // Otherwise, this is a new definition, so we add it to the symbol table
        Type returnType = method.getType();
        if (returnType.getBasicType() instanceof CustomType ct && stringTable.findOrInsertClass(ct.getIdentifier()).getCurrentDef() == null) {
            //TODO: Error: Unknown class
        }
        symbolTable.insert(symbol, new MethodDefinition(symbol, null/* TODO: TYPE? */));
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        // TODO
        SymbolTable symbolTable = null;
        
        // We only have to look at LocalVariableDeclarationStatements, since they are the only ones that create a new Definition
        // All other types only have to be checked for referenced variables in their expressions, which is done in runOnExpression
        if (statement instanceof LocalVariableDeclarationStatement l) {
            Symbol s = stringTable.findOrInsertVariable(l.getIdentifier());
            // If we already have a method with this name, it uses a different symbol, as returned by the string table
            // and we have no conflicting definitions that get overwritten
            symbolTable.insert(s, new VariableDefinition(s, l.getType()));
        }
        
        return false;
    }

    /**
     * Checks whether the given expression references only variables that are already defined.
     * @param expression The expression to check
     * @return Whether this run made any changes that would require other passes to run again
     */
    @Override
    public boolean runOnExpression(Expression expression) {
        // TODO
        SymbolTable symbolTable = null;
        for (Variable v : getReferencedVariables(expression)) {
            // Check if this variable has been defined
            Symbol s = stringTable.findOrInsertVariable(v.getName());
            // We have to check, if the symbol is defined in any parent scope, not just the current.
            if (symbolTable.lookup(s) == null) {
                // TODO: Error: v not defined.
                return false;
            }
        }
        return false;
    }
    
    private List<Variable> getReferencedVariables(Expression ex) {
        // These expressions cannot reference any variables
        if (ex instanceof ErrorExpression || ex instanceof UninitializedValue || ex instanceof VoidExpression) {
            return new LinkedList<>();
        } else if (ex instanceof BinaryExpression b) {
            // Return the variables referenced on the lhs and rhs
            List<Variable> results = getReferencedVariables(b.getLhs());
            results.addAll(getReferencedVariables(b.getRhs()));
            return results;
        } else if (ex instanceof PrimaryExpression p) {
            // These expressions cannot reference any variables
            if (p instanceof NullValue || p instanceof ThisValue || p instanceof BooleanValue
                    || p instanceof IntegerValue || p instanceof NewObjectExpression) {
                return new LinkedList<>();
            } else if (p instanceof NewArrayExpression e) {
                // NewArrayExpression has an expression in the array size that could reference variables
                return getReferencedVariables(e.getSize());
            } else if (p instanceof Variable v) {
                // If we reached a variable, we return it
                LinkedList<Variable> results = new LinkedList<>();
                results.add(v);
                return results;
            } else {
                // If we reach this statement, a new PrimaryExpression subclass has been added
                // that should be considered in the if-statements above
                throw new RuntimeException();
            }
        } else if (ex instanceof UnaryExpression u) {
            // E.g. '-a'
            return getReferencedVariables(u.getEncapsulated());
        } else {
            // If we reach this statement, a new Expression subclass has been added
            // that should be considered in the if-statements above
            throw new RuntimeException();
        }
    }

    @Override
    public PassDependencyType getMinDependencyType() {
        return PassDependencyType.METHOD_PASS;
    }

    @Override
    public PassDependencyType getMaxDependencyType() {
        return PassDependencyType.EXPRESSION_PASS;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

    @Override
    public void registerPassManager(PassManager manager) {
        this.manager = manager;
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
    
}
