package de.dercompiler.pass.passes;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.NullType;

import java.util.List;

public class NullTypeCheckPass implements ExpressionPass {
    private PassManager passManager;
    private GlobalScope globalScope;
    private long id;
    private OutputMessageHandler logger;

    @Override
    public boolean runOnExpression(Expression expression) {

        List<Expression> nullValues = new ReferencesCollector(ReferencesCollector.ReferenceType.NULL_VALUE).analyze(expression);
        nullValues.forEach(nullValue -> {
            NullType type = (NullType) nullValue.getType();
            if (type.getExpectedType() == null)  {
                failSpecs(nullValue, "NullValue has no expected type", false);
            } else {
                //logger.printInfo("null at " + nullValue.getSourcePosition() + " is just fine being a " + type.getExpectedType() + "!");
            }
        });
        return false;
    }

    private void failSpecs(ASTNode node, String message, boolean quit) {
        System.err.println(getPassManager().getLexer().printSourceText(node.getSourcePosition()));

        logger.printErrorAndExit(PassErrorIds.SPECS_VIOLATION, message);
        if (quit) getPassManager().quitOnError();
    }

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
        logger = new OutputMessageHandler(MessageOrigin.PASSES);
    }

    @Override
    public void doFinalization(Program program) {
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(TypeAnalysisPass.class);
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

    @Override
    public void registerPassManager(PassManager manager) {
    this.passManager = manager;
    }

    @Override
    public PassManager getPassManager() {
        return passManager;
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
        return AnalysisDirection.BOTTOM_UP;
    }
}
