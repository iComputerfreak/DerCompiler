package de.dercompiler.pass;

import java.util.LinkedList;
import java.util.List;

public class AnalysisUsage {

    private List<Class<? extends Pass>> analyses;
    private DependencyType type;

    public AnalysisUsage() {
        analyses = new LinkedList<>();
        type = DependencyType.RUN_IN_NEXT_STEP;
    }

    /**
     * sets an Analysis as required for the current class.
     *
     * @param analysis Analysis required for run the Pass
     */
    public void requireAnalysis(Class<? extends Pass> analysis) {
           analyses.add(analysis);
    }

    /**
     * checks if the Pass needs the Analysis.
     *
     * @param needed The Analysis to check
     * @return true, if the Pass needs the Analysis.
     */
    public boolean needsAnalysis(Class<? extends Pass> needed) {
        return analyses.contains(needed);
    }

    public List<Class<? extends Pass>> getAnalyses() { return analyses; }

    public void setDependency(DependencyType type) {
        this.type = type;
    }

    public DependencyType getDependency() {
        return type;
    }
}
