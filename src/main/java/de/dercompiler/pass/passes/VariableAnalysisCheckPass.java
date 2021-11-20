package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.Symbol;
import de.dercompiler.semantic.SymbolTable;
import de.dercompiler.semantic.type.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks for any reference to variables whether they are defined in their scope.
 */
public class VariableAnalysisCheckPass implements ClassPass, MethodPass, StatementPass, ExpressionPass {

    private SymbolTable symbolTable;

    private StringTable stringTable;
    private GlobalScope globalScope;

    @Override
    public void doInitialization(Program program) {
        // Get the symbol table from the Program.
        // We only need a single SymbolTable for the whole analysis, since we can differentiate between
        // Symbols for variables and symbols for methods via the StringTables.
        this.symbolTable = program.getSymbolTable();
        this.globalScope = program.getGlobalScope();
    }

    @Override
    public void doFinalization(Program program) {
    }

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
        if (statement instanceof LocalVariableDeclarationStatement decl) {
            if (decl.getType().getBasicType() instanceof CustomType customType) {
                if (!globalScope.hasClass(customType.getIdentifier())) {
                    // error: Type of new variable unknown
                }
            }
            decl.setRefType(TypeFactory.getInstance().create(decl.getType()));

            insert(decl.getIdentifier(), decl);
        }

        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {

        List<Expression> references = new ReferencesCollector().analyze(expression);

        for (Expression ex : references) {
            if (ex instanceof Variable variable) {
                if (!stringTable.contains(variable.getName())) {
                    new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.UNDEFINED_VARIABLE, "Variable %s is unknown".formatted(variable.getName()));
                }
                variable.setDefinition(stringTable.findOrInsert(variable.getName()).getCurrentDef());
                variable.setType(variable.getDefinition().getRefType());
            } else if (ex instanceof MethodInvocationOnObject call) {
                Type refObj = call.getReferenceObject().getType();
                if (refObj instanceof ClassType cObj) {
                    if (!cObj.hasMethod(call.getFunctionName())) {
                        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.UNKNOWN_METHOD, "Unknown method \"%s\" on %s object".formatted(cObj.getIdentifier()));
                    }
                    call.setType(cObj.getMethod(call.getFunctionName()).getReferenceType().getReturnType());

                } else {
                    new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.ILLEGAL_METHOD_CALL, "Cannot invoke method on %s object".formatted(call.getReferenceObject().getType()));
                }
            } else if (ex instanceof FieldAccess field) {
                Type refObj = field.getEncapsulated().getType();
                if (refObj instanceof ClassType cObj) {
                    if (!cObj.hasField(field.getFieldName())) {
                        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.UNKNOWN_FIELD, "Unknown field on %s object".formatted(cObj.getIdentifier()));
                    }
                    field.setType(cObj.getField(field.getFieldName()).getRefType());
                } else {
                    new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.ILLEGAL_FIELD_REFERENCE, "Cannot access field on %s object".formatted(field.getFieldName()));
                }
            } else if (ex instanceof ArrayAccess arrayAccess) {
                ArrayType type = (ArrayType) arrayAccess.getEncapsulated().getType();
                arrayAccess.setType(type.getElementType());
            } else if (ex instanceof ThisValue) {
                ClassDeclaration classDeclaration = getPassManager().getCurrentClass();
                ClassType type = globalScope.getClass(classDeclaration.getIdentifier());
                ex.setType(type);
            }
        }

        return false;
    }

    private void insert(String identifier, ASTDefinition definition) {
        insert(identifier, definition, false);
    }

    private void insert(String identifier, ASTDefinition definition, boolean inOutestScope) {
        Symbol symbol = stringTable.findOrInsert(identifier);
        if (symbolTable.isDefinedInCurrentScope(symbol)) {
            //Error, da identifier in diesem Scope schon definiert wurde
        }
        if (!inOutestScope && symbolTable.isDefinedInNotOutestScope(symbol)) {
            //Error, da identifier schon definiert wurde und nicht im äußersten scope (klassenvariablen)
        }

        symbolTable.insert(symbol, definition);
    }


    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(EnterScopePass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
