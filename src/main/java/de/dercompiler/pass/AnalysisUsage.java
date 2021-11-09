package de.dercompiler.pass;

import java.util.LinkedList;
import java.util.List;

public class AnalysisUsage {

    private List<Class<AnalysisPass>> analyses;

    public AnalysisUsage() {
        analyses = new LinkedList<>();
    }

    /**
     * sets an Analysis as required for the current class.
     *
     * @param analysis Analysis required for run the Pass
     */
    public void requireAnalysis(Class<AnalysisPass> analysis) {
           analyses.add(analysis);
    }

    /**
     * checks if the Pass needs the Analysis.
     *
     * @param needed The Analysis to check
     * @return true, if the Pass needs the Analysis.
     */
    public boolean needsAnalysis(Class<AnalysisPass> needed) {
        return analyses.contains(needed);
    }

    public List<Class<AnalysisPass>> getAnalyses() { return analyses; }
}
