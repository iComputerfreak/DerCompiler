package de.dercompiler.pass.passes;

import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.Program;
import de.dercompiler.ast.expression.GlobalConstant;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.*;
import de.dercompiler.semantic.type.*;

public class GlobalsPass implements ClassPass {

    private boolean done = false;
    private GlobalScope globalScope;
    private SymbolTable symbolTable;
    private StringTable stringTable;

    @Override
    public boolean shouldRunOnClass(ClassDeclaration classDeclaration) {
        return !done;
    }

    private void initializeSystemOut() {
        // Set up System.out (overridable)
        ASTDefinition systemDef = new GlobalConstant("System");

        Symbol symbol = stringTable.findOrInsert("System");
        symbolTable.insert(symbol, systemDef);
    }

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        // Only add the object 'System' if there is no custom class "System" present,
        // i.e. if the globalScope entry is not overwritten
        if (globalScope.getClass("System") instanceof InternalClass) initializeSystemOut();

        this.done = true;
        return false;
    }

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
        symbolTable = program.getSymbolTable();
        stringTable = program.getStringTable();
        initializeGlobals();
    }

    @Override
    public void doFinalization(Program program) {

    }

    private void initializeGlobals() {
        // Set up String (not overridable)
        globalScope.addClass(new ClassType("String"));
        ClassType tSystem = new InternalClass("System");
        ClassType tSystemOut = new InternalClass("SystemOut");
        ClassType tSystemIn = new InternalClass("SystemIn");

        FieldDefinition systemOut = new FieldDefinition("out", tSystemOut, tSystem);
        tSystem.addField(systemOut);

        FieldDefinition systemIn = new FieldDefinition("in", tSystemIn, tSystem);
        tSystem.addField( systemIn);
        globalScope.addClass(tSystem);

        MethodDefinition println = new MethodDefinition("println", new MethodType(new VoidType(), new IntegerType()), tSystemOut);
        tSystemOut.addMethod( println);

        MethodDefinition write = new MethodDefinition("write", new MethodType(new VoidType(), new IntegerType()), tSystemOut);
        tSystemOut.addMethod( write);

        MethodDefinition flush = new MethodDefinition("flush", new MethodType(new VoidType()), tSystemOut);
        tSystemOut.addMethod( flush);

        MethodDefinition read = new MethodDefinition("read", new MethodType(new IntegerType()), tSystemIn);
        tSystemIn.addMethod( read);

        globalScope.addClass(tSystemOut);
        globalScope.addClass(tSystemIn);
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(MemberDeclarationPass.class);
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
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
