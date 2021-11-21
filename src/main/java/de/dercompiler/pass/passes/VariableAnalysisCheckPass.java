package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.Symbol;
import de.dercompiler.semantic.SymbolTable;
import de.dercompiler.semantic.type.*;

import java.util.List;

/**
 * Checks for any reference to variables whether they are defined in their scope.
 */
public class VariableAnalysisCheckPass implements ClassPass, MethodPass, StatementPass, ExpressionPass {

    private SymbolTable symbolTable;

    private StringTable stringTable;
    private GlobalScope globalScope;
    private OutputMessageHandler logger;

    @Override
    public void doInitialization(Program program) {
        // Get the symbol table from the Program.
        // We only need a single SymbolTable for the whole analysis, since we can differentiate between
        // Symbols for variables and symbols for methods via the StringTables.
        this.logger = new OutputMessageHandler(MessageOrigin.PASSES);
        this.symbolTable = program.getSymbolTable();
        this.globalScope = program.getGlobalScope();

        initializeString();
    }

    private void initializeString() {
        // Set up String (not overridable)
        globalScope.addClass(new ClassType("String"));
        ClassType systemType = new LibraryClass("System");
        Field systemOut1 = new Field(null, new de.dercompiler.ast.type.Type(null, new CustomType(null, "SystemOut"), 0), "out");
        systemType.addField("out", systemOut1);

        Field systemIn1 = new Field(null, new de.dercompiler.ast.type.Type(null, new CustomType(null, "SystemIn"), 0), "in");
        systemType.addField("in", systemIn1);
        globalScope.addClass(systemType);

        ClassType systemOut = new LibraryClass("SystemOut");
        ClassType systemIn = new LibraryClass("SystemIn");

        Method println = new Method(null, null, "println", null, null, null);
        println.setReferenceType(new MethodType(new VoidType(), List.of(new IntegerType()), false));
        systemOut.addMethod("println", println);

        Method write = new Method(null, null, "write", null, null, null);
        write.setReferenceType(new MethodType(new VoidType(), List.of(new IntegerType()), false));
        systemOut.addMethod("write", write);

        Method flush = new Method(null, null, "flush", null, null, null);
        flush.setReferenceType(new MethodType(new VoidType(), List.of(), false));
        systemOut.addMethod("flush", flush);

        Method read = new Method(null, new de.dercompiler.ast.type.Type(null, new CustomType(null,"String"), 0), "read", null, null, null);
        read.setReferenceType(new MethodType(new AnyType(), List.of(), false));
        systemIn.addMethod("read", read);

        globalScope.addClass(systemOut);
        globalScope.addClass(systemIn);
    }

    private void initializeSystemOut() {
        // Set up System.out (overridable)
        Symbol systemSymbol = stringTable.findOrInsert("System");
        ASTDefinition systemDef = new GlobalConstant("System");


        symbolTable.insert(systemSymbol, systemDef);
    }

    @Override
    public void doFinalization(Program program) {
    }


    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        stringTable = new StringTable();

        // Do not add the object 'System' if there is a custom class System present
        if (globalScope.getClass("System") instanceof LibraryClass) initializeSystemOut();

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
            BasicType basicType = parameter.getType().getBasicType();
            if (basicType instanceof de.dercompiler.ast.type.VoidType) {
                failVariableAnalysis(PassErrorIds.ILLEGAL_PARAMETER_TYPE, "Illegal type '%s' for method parameter", parameter.getSourcePosition());
            }
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
                    failVariableAnalysis(PassErrorIds.UNKNOWN_TYPE, "Type %s of new variable is unknown".formatted(customType.getIdentifier()), decl.getSourcePosition());
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
                    failVariableAnalysis(PassErrorIds.UNDEFINED_VARIABLE, "Variable %s is unknown".formatted(variable.getName()), variable.getSourcePosition());
                    continue;
                }
                variable.setDefinition(stringTable.findOrInsert(variable.getName()).getCurrentDef());
                variable.setType(variable.getDefinition().getRefType());
            } else if (ex instanceof MethodInvocationOnObject call) {
                Type refObj = call.getReferenceObject().getType();
                if (refObj instanceof ClassType objType) {
                    if (!objType.hasMethod(call.getFunctionName())) {
                        failVariableAnalysis(PassErrorIds.UNKNOWN_METHOD, "Unknown method \'%s\' of %s object".formatted(call.getFunctionName(), objType.getIdentifier()), call.getSourcePosition());
                        continue;
                    }
                    call.setType(objType.getMethod(call.getFunctionName()).getReferenceType().getReturnType());

                } else {
                    failVariableAnalysis(PassErrorIds.ILLEGAL_METHOD_CALL, "Cannot invoke method on %s object".formatted(call.getReferenceObject().getType()), ex.getSourcePosition());
                    continue;
                }
            } else if (ex instanceof FieldAccess field) {
                Type refType = field.getEncapsulated().getType();
                if (refType instanceof ClassType cObj) {
                    if (!cObj.hasField(field.getFieldName())) {
                        failVariableAnalysis(PassErrorIds.UNKNOWN_FIELD, "Unknown field \'%s\' of %s object".formatted(field.getFieldName(), cObj.getIdentifier()), field.getSourcePosition());
                        continue;
                    }
                    field.setType(cObj.getField(field.getFieldName()).getRefType());
                } else {
                    failVariableAnalysis(PassErrorIds.ILLEGAL_FIELD_REFERENCE, "Cannot access field on %s object".formatted(field.getFieldName()), field.getSourcePosition());
                    continue;
                }
            } else if (ex instanceof ArrayAccess arrayAccess) {
                Type innerType = arrayAccess.getEncapsulated().getType();
                if (innerType instanceof ArrayType type) {
                    arrayAccess.setType(type.getElementType());
                } else {
                    failVariableAnalysis(PassErrorIds.ILLEGAL_ARRAY_ACCESS, "Illegal array access on non-array object", arrayAccess.getSourcePosition());
                }
            } else if (ex instanceof ThisValue) {
                ClassDeclaration classDeclaration = getPassManager().getCurrentClass();
                ClassType type = globalScope.getClass(classDeclaration.getIdentifier());
                ex.setType(type);
            } else if (ex instanceof NewArrayExpression newArray) {
                newArray.setType(TypeFactory.getInstance().createArrayType(newArray.getBasicType(), newArray.getDimension()));
            } else if (ex instanceof NewObjectExpression newObject) {
                newObject.setType(TypeFactory.getInstance().create(newObject.getObjectType()));
            } else if (ex instanceof AssignmentExpression ass) {
                Type type = ass.getRhs().getType();
                if (type == null) type = ass.getLhs().getType();
                ex.setType(type);
            }
        }

        return false;
    }

    private void failVariableAnalysis(PassErrorIds errorId, String msg, SourcePosition position) {
        System.err.println(getPassManager().getLexer().printSourceText(position));
        logger.printErrorAndExit(errorId, msg);
        getPassManager().quitOnError();
    }

    private void insert(String identifier, ASTDefinition definition) {
        insert(identifier, definition, false);
    }

    private void insert(String identifier, ASTDefinition definition, boolean inOutestScope) {
        Symbol symbol = stringTable.findOrInsert(identifier);
        if (symbolTable.isDefinedInCurrentScope(symbol)) {
            failVariableAnalysis(PassErrorIds.DUPLICATE_VARIABLE, "Variable %s is already defined in this scope".formatted(identifier), definition.getSourcePosition());
        }
        if (!inOutestScope && symbolTable.isDefinedInsideCurrentMethod(symbol)) {
            //Error, da identifier schon definiert wurde und nicht im äußersten scope (klassenvariablen)
            failVariableAnalysis(PassErrorIds.DUPLICATE_VARIABLE, "Variable %s is already defined in this method".formatted(identifier), definition.getSourcePosition());
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
