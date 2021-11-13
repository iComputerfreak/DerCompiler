package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.Type;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.*;

import java.util.LinkedList;
import java.util.List;

public class VariableAnalysisCheckPass implements ClassPass {
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        SymbolTable symbolTable = new SymbolTable();

        StringTable stringTable = new StringTable();
        //Felderdeklarationen von Klassen müssen noch anders Behandelt werden als Felderdeklarationen in Methoden, da letzeres nicht geschachtelt stattfinden darf
        //!

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
            BasicBlock basicBlock = (BasicBlock) statement;
            symbolTable.enterScope();

            for(Statement basicBlockStatement: basicBlock.getStatements()){
                visitStatement(basicBlockStatement, symbolTable, stringTable);
            }

            symbolTable.leaveScope();
        } else if (statement instanceof IfStatement){
            IfStatement ifStatement = (IfStatement) statement;

            visitExpression(ifStatement.getCondition(), symbolTable, stringTable);

            symbolTable.enterScope();
            visitStatement(ifStatement.getThenStatement(), symbolTable, stringTable);
            symbolTable.leaveScope();
            symbolTable.enterScope();
            visitStatement(ifStatement.getElseStatement(), symbolTable, stringTable);
            symbolTable.leaveScope();
        } else if (statement instanceof LocalVariableDeclarationStatement){
            LocalVariableDeclarationStatement localVariableDeclarationStatement = (LocalVariableDeclarationStatement) statement;

            insert(localVariableDeclarationStatement.getIdentifier(), localVariableDeclarationStatement.getType(), symbolTable, stringTable);
        } else if (statement instanceof ReturnStatement){
            ReturnStatement returnStatement = (ReturnStatement) statement;

            visitExpression(returnStatement.getExpression(), symbolTable, stringTable);
        } else if (statement instanceof WhileStatement){
            WhileStatement whileStatement = (WhileStatement) statement;

            visitExpression(whileStatement.getCondition(), symbolTable, stringTable);

            symbolTable.enterScope();
            visitStatement(whileStatement.getStatement(), symbolTable, stringTable);
            symbolTable.leaveScope();
        }

    }

    private void visitExpression(Expression expression, SymbolTable symbolTable, StringTable stringTable){
        List<Variable> referencedVariables = getReferencedVariables(expression);

        for(Variable variable: referencedVariables){
            if (!stringTable.contains(variable.getName())){
                //Error, da referenzierte Variable nicht existiert
            }
        }
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
