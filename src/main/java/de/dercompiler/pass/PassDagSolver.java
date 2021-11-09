package de.dercompiler.pass;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.*;
import java.util.function.Function;

public class PassDagSolver {

    public static List<Pass> solveDependencies(List<Pass> passes) {
        Pass[] vertices = passes.toArray(new Pass[0]);
        AnalysisUsage[] edges = new AnalysisUsage[vertices.length];
        int[] count = new int[vertices.length];

        //TODO is there a smarter and faster way than linear search?
        for(int i = 0; i < vertices.length; i++) {
            edges[i] = vertices[i].getAnalysisUsage(new AnalysisUsage());
            List<Pass> deps = PassHelper.transform(edges[i].getAnalyses(), PassHelper.AnalysisUsageToPass);
            for (Pass dep : deps) {
                boolean found = false;
                for (int k = 0; k < vertices.length; k++) {
                    if (dep.getID() == vertices[k].getID()) {
                        found = true;
                        count[k]++;
                    }
                }

                if (!found) {
                    new OutputMessageHandler(MessageOrigin.PASSES).internalError("Something is wrong with the internal registration of passes, pls check the implementation for adding missing but necessary ones!");
                }
            }
        }

        LinkedList<Pass> resolvedOrder = new LinkedList<>();

        Queue<Pass> S = new LinkedList<>();
        Queue<Integer> idx = new LinkedList<>();
        for (int i = 0; i < vertices.length; i++) {
            if (count[i] == 0) {
                S.add(vertices[i]);
                idx.add(i);
            }
        }

        while (!S.isEmpty()) {
            if(S.size() != idx.size()) {
                new OutputMessageHandler(MessageOrigin.PASSES).internalError("Kahn's Algorithm to sort Passes is broken");
            }

            Pass pass = S.remove();
            //here we create the ordering
            resolvedOrder.addFirst(pass);
            int i = idx.remove();
            List<Pass> deps = PassHelper.transform(edges[i].getAnalyses(), PassHelper.AnalysisUsageToPass);
            for (Pass dep : deps) {
                for (int k = 0; k < vertices.length; k++) {
                    if (dep.getID() == vertices[k].getID()) {
                        count[k]--;
                        if (count[k] == 0) {
                            S.add(vertices[k]);
                            idx.add(k);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < vertices.length; i++) {
            if (count[i] != 0) {
                new OutputMessageHandler(MessageOrigin.PASSES). internalError("We have a cycle in our Pass dependencies: " + vertices[i].getClass().getName());
                return null;
            }
        }
        return resolvedOrder;
    }
}
