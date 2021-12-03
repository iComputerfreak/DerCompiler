package de.dercompiler.transformation;

import firm.Dump;
import firm.Graph;
import firm.nodes.Node;

public class GraphDumper {

    public static long n = 0;

    private static void dumpGraph(TransformationState state, String extension) {
        System.out.println("Dumped graph: "+ state.graph.getEntity().getName() + "_" + extension);
        Dump.dumpGraph(state.graph, "_" + extension);

    }

    public static void dumpGraph(TransformationState state) {
        dumpGraph(state, "" + n++);
    }

    public static void dumpGraphFinal(TransformationState state) {
        dumpGraph(state, "final");
        n = 0;
    }
}
