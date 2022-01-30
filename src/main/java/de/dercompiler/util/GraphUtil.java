package de.dercompiler.util;

import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import firm.nodes.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;

public class GraphUtil {
    public static <T, E> List<T> getPredecessors(T a, Graph<T, E> graph) {
        return graph.incomingEdgesOf(a).stream().map(graph::getEdgeSource).toList();
    }

    public static <T, E> List<T> getSuccessors(T a, Graph<T, E> graph) {
        return graph.outgoingEdgesOf(a).stream().map(graph::getEdgeTarget).toList();
    }

    public static <T, E> T getOtherPred(T block, T pred, Graph<T, E> graph) {
        List<T> predecessors = new ArrayList<>(getPredecessors(block, graph));
        predecessors.remove(pred);
        return predecessors.isEmpty() ? null : predecessors.get(0);
    }

    public static <T, E> T getOtherSucc(T block, T succ, Graph<T, E> graph) {
        List<T> successors = new ArrayList<>(getSuccessors(block, graph));
        successors.remove(succ);
        return successors.isEmpty() ? null : successors.get(0);
    }
}
