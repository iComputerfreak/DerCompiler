package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PassManager {

    List<Pass> passes;

    private void addPassAfterCheck(Pass pass) {
        passes.add(pass);
    }

    /**
     * Add a Pass to the PassManager, this assures the correct ordering to run the passes defined by the getAnalysisUsage, but in general every pass gets added to the end.
     *
     * @param pass to add to the pipeline
     */
    public void addPass(Pass pass) {
        if (Objects.isNull(pass)) new OutputMessageHandler(MessageOrigin.PASSES)
                .printWarning(PassWarningIds.NULL_AS_PASS_NOT_ALLOWED,"Something may be wrong with the compiler we tried to add a null value as Pass, please report your current setup to the Developers.");
        if (pass instanceof ClassPass cp) addPassAfterCheck(cp);
        else if (pass instanceof MethodPass mp) addPassAfterCheck(mp);
        else if (pass instanceof StatementPass sp) addPassAfterCheck(sp);
        else if (pass instanceof ExpressionPass ep) addPassAfterCheck(ep);
        else
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("can't add Pass: " + pass.getClass() + " the type is not implemented currently!");
    }

    private PassPipeline generateOrder(List<Pass> passes) {
        List<Pass> ordered = PassDagSolver.solveDependencies(passes);
        if (Objects.isNull(ordered)) {
            return new PassPipeline(new LinkedList<>());
        }
        return new PassPipeline(ordered);
    }

    private void initializeMissingPasses() {
        HashSet<Long> ids = new HashSet<>();
        for (Pass pass : passes) {
            ids.add(pass.getID());
        }
        for (Pass pass : passes) {
            List<Pass> deps = PassHelper.transform(pass.getAnalysisUsage(new AnalysisUsage()).getAnalyses(), PassHelper.AnalysisUsageToPass);
            for (Pass dep : deps) {
                if (!ids.contains(dep.getID())) {
                    ids.add(dep.getID());
                    passes.add(dep);
                }
            }
        }
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
        traverseTree(generateOrder(passes), program);
        finalizePasses(program);
    }

    private class PassPipeline {

        LinkedList<List<Pass>> pipeline;

        public PassPipeline(List<Pass> passes) {

            pipeline = new LinkedList<>();
            LinkedList<Pass> step = new LinkedList<>();

            PassDependencyType last = null;

            for (Pass pass : passes) {
                boolean newStep =
                        !pass.getMinDependencyType().usesNaturalOrdering() || Objects.isNull(last)
                        || !last.usesNaturalOrdering()
                        || last.ordinal() > pass.getMinDependencyType().ordinal();

                if (newStep && !step.isEmpty()) {
                    pipeline.addLast(step);
                    step = new LinkedList<>();
                }
                step.add(pass);
                last = pass.getMaxDependencyType();
            }
            if (!step.isEmpty()) {
                pipeline.addLast(step);
            }
        }


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
