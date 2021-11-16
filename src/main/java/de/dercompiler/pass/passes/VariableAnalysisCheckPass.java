package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BooleanType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.pass.AnalysisUsage;
import de.dercompiler.pass.ClassPass;
import de.dercompiler.pass.PassManager;
import de.dercompiler.semantic.*;
import de.dercompiler.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * hier werden für jede Klasse ihre Variablendeklarationen überprüft
 */
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

                insert(field.getIdentifier(), field.getType(), symbolTable, stringTable, true);


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
                    visitStatement(statement, symbolTable, stringTable, method.getType());
                }

            }
        }
        return false;
    }

    private void visitStatement(Statement statement, SymbolTable symbolTable, StringTable stringTable, Type methodtype){
        if (statement instanceof BasicBlock){
            BasicBlock basicBlock = (BasicBlock) statement;
            symbolTable.enterScope();

            for(Statement basicBlockStatement: basicBlock.getStatements()){
                visitStatement(basicBlockStatement, symbolTable, stringTable, methodtype);
            }

            symbolTable.leaveScope();
        } else if (statement instanceof IfStatement){
            IfStatement ifStatement = (IfStatement) statement;

            if (!BooleanType.class.isInstance(ifStatement.getCondition().getType())){
                //Error, da expression kein boolscher Wert
            }

            visitExpression(ifStatement.getCondition(), symbolTable, stringTable);

            symbolTable.enterScope();
            visitStatement(ifStatement.getThenStatement(), symbolTable, stringTable, methodtype);
            symbolTable.leaveScope();
            symbolTable.enterScope();
            visitStatement(ifStatement.getElseStatement(), symbolTable, stringTable, methodtype);
            symbolTable.leaveScope();
        } else if (statement instanceof LocalVariableDeclarationStatement){
            LocalVariableDeclarationStatement localVariableDeclarationStatement = (LocalVariableDeclarationStatement) statement;

            insert(localVariableDeclarationStatement.getIdentifier(), localVariableDeclarationStatement.getType(), symbolTable, stringTable);

            visitExpression(localVariableDeclarationStatement.getExpression(), symbolTable, stringTable);

            if (localVariableDeclarationStatement.getExpression().getType()
                    .syntaxEquals(localVariableDeclarationStatement.getType())){
                //Error, da linke seite und rechte Seite unterschiedlicher Typ
            }
        } else if (statement instanceof ReturnStatement){
            ReturnStatement returnStatement = (ReturnStatement) statement;

            if (!methodtype.syntaxEquals(returnStatement.getExpression().getType())){
                //Error da Rpckgabewert nicht mit methode übereinstimmt
            }

            visitExpression(returnStatement.getExpression(), symbolTable, stringTable);
        } else if (statement instanceof WhileStatement){
            WhileStatement whileStatement = (WhileStatement) statement;

            if (!BooleanType.class.isInstance(whileStatement.getCondition().getType())){
                //Error, da expression kein boolscher Wert
            }

            visitExpression(whileStatement.getCondition(), symbolTable, stringTable);

            symbolTable.enterScope();
            visitStatement(whileStatement.getStatement(), symbolTable, stringTable, methodtype);
            symbolTable.leaveScope();
        }

    }

    private void visitExpression(Expression expression, SymbolTable symbolTable, StringTable stringTable){
        List<Variable> referencedVariables = Utils.getReferencedVariables(expression);

        for(Variable variable: referencedVariables){
            if (!stringTable.contains(variable.getName())){
                //Error, da referenzierte Variable nicht existiert
            }
        }
    }

    private void insert(String identifier, Type type, SymbolTable symbolTable, StringTable stringTable){
        insert(identifier, type, symbolTable, stringTable, false);
    }

    private void insert(String identifier, Type type, SymbolTable symbolTable, StringTable stringTable, boolean inOutestScope){
        Symbol symbol = stringTable.findOrInsert(identifier);
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
