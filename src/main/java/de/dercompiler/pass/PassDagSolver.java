package de.dercompiler.pass;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.*;

public class PassDagSolver {

    /**
     * this method uses an implementation of Kahn's Algorithm to solve the ordering in a DAG in O(n)
     *
     * @param passes the passes aka the vertices of the graph, the edges are defined by the AnalysisUsage defined by the pass itself
     * @return the ordered Pass based on the defined underlying DAG
     */
    public static PassPipeline solveDependencies(List<Pass> passes, PassManager manager) {
        Pass[] vertices = passes.toArray(new Pass[0]);
        ArrayList<List<Pass>> edges = new ArrayList<List<Pass>>(vertices.length);
        int[] count = new int[vertices.length];
        Map<Long, Integer> lookupTable = new HashMap<>();

        for(int i = 0; i < vertices.length; i++) {
            lookupTable.put(vertices[i].getID(), i);
            edges.set(i, PassHelper.transform(vertices[i].getAnalysisUsage(new AnalysisUsage()).getAnalyses(), PassHelper.AnalysisUsageToPass));
        }

        for (int i = 0; i < vertices.length; i++) {
            for (Pass dep : edges.get(i)) {
                if (lookupTable.containsKey(dep.getID())) {
                    int idx = lookupTable.get(dep.getID());
                    count[idx] += 1;
                } else {
                    new OutputMessageHandler(MessageOrigin.PASSES)
                            .internalError("Something is wrong with the internal registration of passes, pls check the implementation for adding missing but necessary ones!");
                }
            }
        }

        Queue<Pass> next = new LinkedList<>();
        Queue<Pass> current;
        Queue<Integer> nextIdx = new LinkedList<>();
        Queue<Integer> currentIdx;

        PassPipeline pipeline = new PassPipeline(manager);

        for (int i = 0; i < vertices.length; i++) {
            if (count[i] == 0) {
                next.add(vertices[i]);
                nextIdx.add(i);
            }
        }

        while (!next.isEmpty()) {
            current = next;
            next = new LinkedList<>();
            currentIdx = nextIdx;
            nextIdx = new LinkedList<>();
            while (!current.isEmpty()) {
                if (current.size() != currentIdx.size()) {
                    new OutputMessageHandler(MessageOrigin.PASSES).internalError("Kahn's Algorithm to sort Passes is broken");
                }
                Pass pass = current.remove();
                int i = currentIdx.remove();
                pipeline.addPass(pass);
                // we know dep exists in the lookupTable because of the check above
                for (Pass dep : edges.get(i)) {
                    int idx = lookupTable.get(dep.getID());
                    count[idx]--;
                    if (count[idx] == 0) {
                        next.add(vertices[idx]);
                        nextIdx.add(idx);
                    }
                }
            }
            if (next.size() > 0) {
                pipeline.nextStep();
            }
        }
        for (int i = 0; i < vertices.length; i++) {
            if (count[i] != 0) {
                new OutputMessageHandler(MessageOrigin.PASSES). internalError("We have a cycle in our Pass dependencies: " + vertices[i].getClass().getName());
                return null;
            }
        }
        return pipeline;
    }
}
