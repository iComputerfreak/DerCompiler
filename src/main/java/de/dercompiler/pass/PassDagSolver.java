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
        ArrayList<List<Pass>> edges = new ArrayList<>(vertices.length);
        ArrayList<List<Pass>> directAfter = new ArrayList<>(vertices.length);
        int[] count = new int[vertices.length];
        DependencyType[] depType = new DependencyType[vertices.length];
        Arrays.fill(depType, DependencyType.RUN_DIRECTLY_AFTER);
        Map<Long, Integer> lookupTable = new HashMap<>();

        for(int i = 0; i < vertices.length; i++) {
            lookupTable.put(vertices[i].getID(), i);
            AnalysisUsage usage = vertices[i].getAnalysisUsage(new AnalysisUsage());
            edges.add(i, PassHelper.transform(usage.getAnalyses(), PassHelper.AnalysisUsageToPass));
            depType[i] = usage.getDependency();
            directAfter.add(i, new LinkedList<>());
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

        PassPipeline pipeline = new PassPipeline(manager);

        for (int i = 0; i < vertices.length; i++) {
            if (count[i] == 0) {
                next.add(vertices[i]);
            }
        }

        while (!next.isEmpty()) {
            current = next;
            next = new LinkedList<>();
            while (!current.isEmpty()) {
                Pass pass = current.remove();
                int i = lookupTable.get(pass.getID());

                List<? extends Pass> deps = edges.get(i);
                if (depType[i] == DependencyType.RUN_DIRECTLY_AFTER && deps.size() != 0) {
                    /* moved to else
                    if (deps.size() == 0) {
                        pipeline.addPass(pass);
                    }
                     */
                    if (deps.size() == 1) {
                        List<Pass> dA = directAfter.get(lookupTable.get(deps.get(0).getID()));
                        if (pass.getAnalysisDirection() == AnalysisDirection.TOP_DOWN && deps.get(0).getAnalysisDirection() == AnalysisDirection.BOTTOM_UP) {
                            new OutputMessageHandler(MessageOrigin.PASSES).internalError("Invalid dependency: Pass " + pass.getClass().getName() + " is TOP_DOWN and pass " + deps.get(0).getClass().getName() + " is BOTTOM_UP, we can't run the directly after eachother.");
                        }
                        dA.add(pass);
                        dA.addAll(directAfter.get(i));
                        directAfter.get(i).clear();
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (Pass dep : deps) {
                            sb.append(dep.getClass().getName()).append(", ");
                        }
                        new OutputMessageHandler(MessageOrigin.PASSES).internalError( "Pass: " + pass.getClass().getName() + " has more than one Predecessor: " + sb);
                    }
                } else {
                    pipeline.addPass(pass);
                    for (Pass dep : directAfter.get(i)) {
                        pipeline.addPass(dep);
                    }
                }
                // we know dep exists in the lookupTable because of the check above
                for (Pass dep : deps) {
                    int idx = lookupTable.get(dep.getID());
                    count[idx]--;
                    if (count[idx] == 0) {
                        next.add(vertices[idx]);
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
