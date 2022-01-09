package de.dercompiler.transformation;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Dump;
import firm.Graph;
import firm.nodes.Node;

public class GraphDumper {

    public static long n = 0;
    public static boolean dump_graph = false;

    private static void dumpGraph(TransformationState state, String extension) {
        if (dump_graph) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).printInfo("Dumped graph: " + state.graph.getEntity().getName() + "_" + extension);
            Dump.dumpGraph(state.graph, "_" + extension);
        }
    }

    public static void dumpGraph(TransformationState state) {
        dumpGraph(state, "" + n++);
    }

    public static void dumpGraphFinal(TransformationState state) {
        dumpGraph(state, "final");
        n = 0;
    }

    public static void dump(boolean active) {
        dump_graph = active;
    }
}
