package de.dercompiler.pass;

import de.dercompiler.ast.Program;

public sealed interface Pass permits AnalysisPass, ClassPass, ExpressionPass, MethodPass, StatementPass {

    /**
     * DO necessary Initialization before running the Passes.
     *
     * @param program Program to run the Pass on.
     */
    void doInitialization(Program program);

    /**
     *  Do necessary Finalization after the Passes run.
     *
     * @param program Program the tests run on.
     */
    void doFinalization(Program program);

    /**
     * This provides a strict ordering in which the Passes had to run, if we require a Analysis it is assured that the
     * current Pass is executed after the Analysis, so the Results for the Current ASTNode are ready for usage
     *
     * @param usage AnalysisUsage to add the required Analyses
     * @return the AnalysisUsage with all required Analyses
     */
    AnalysisUsage getAnalysisUsage(AnalysisUsage usage);

    /**
     * This provides a AnalysisUsage with all Analyses that got invalidated because of the current run,
     * we have to rerun them for this ASTNode again.
     *
     * @param usage AnalysisUsage to add the invalidated Analyses
     * @return the AnalysisUsage
     */
    AnalysisUsage invalidatesAnalysis(AnalysisUsage usage);

    /**
     * register the PassManager so it is available for providing information
     *
     * @param manager PassManager that runs this Pass
     */
    void registerPassManager(PassManager manager);

    long registerID(long id);
    long getID();

    default PassDependencyType getMinDependencyType() {
        return PassDependencyType.SEPARATE_PASS;
    }

    default PassDependencyType getMaxDependencyType() {
        return PassDependencyType.SEPARATE_PASS;
    }
}
