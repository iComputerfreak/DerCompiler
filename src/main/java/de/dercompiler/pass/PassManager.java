package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.List;

public class PassManager {

    List<Pass> passes;

    private <PassType extends Pass> void addPassInOrder(PassType pass) {
        AnalysisUsage usage = pass.getAnalysisUsage(new AnalysisUsage());
        //todo order pass into collection, but in the right location
    }

    /**
     * Add a Pass to the PassManager, this assures the correct ordering to run the passes defined by the getAnalysisUsage, but in general every pass gets added to the end.
     *
     * @param pass to add to the pipeline
     */
    public void addPass(Pass pass) {
        if (pass instanceof ClassPass cp) addPass(cp);
        else if (pass instanceof MethodPass mp) addPass(mp);
        else if (pass instanceof StatementPass sp) addPass(sp);
        else if (pass instanceof ExpressionPass ep) addPass(ep);
        else
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("can't add Pass: " + pass.getClass() + " the type is not implemented currently!");
    }

    private PassPipeline generateOrder() {
        PassPipeline pipeline = new PassPipeline();

        return pipeline;
    }

    private void initializeMissingPasses() {

    }

    private void initializePasses(Program program) {
        for (Pass pass : passes) {
            pass.registerPassManager(this);
            pass.doInitialization(program);
        }
    }

    private void finalizePasses(Program program) {
        for (Pass pass : passes) pass.doFinalization(program);
    }

    private void traverseTree(PassPipeline pipeline, Program program) {

    }

    /**
     * Runs the pipeline of passes over the provided Program.
     *
     * @param program Program to run passes on
     */
    public void run(Program program) {
        initializeMissingPasses();
        initializePasses(program);
        traverseTree(generateOrder(), program);
        finalizePasses(program);
    }

    private class PassPipeline {

    }

    /**
     * Returns the current Class-Declaration of the AST.
     *
     * @return the current Class-Declaration
     */
    public ClassDeclaration getCurrentClass() {
        return null;
    }

    /**
     * Returns the current Method of the AST.
     *
     * @return the current Method
     */
    public Method getCurrentMethod() {
        return null;
    }

    /**
     * Returns the current Statement of the AST.
     *
     * @return the current Statement
     */
    public Statement getCurrentStatement() {
        return null;
    }

    /**
     * Returns the current Expression of the AST.
     * Note, because Operations aren't nested(only for Method-calls) this might only return the current Expression we're already looking at.
     *
     * @return the current Expression
     */
    public Expression getCurrentExpression() {
        return null;
    }
}
