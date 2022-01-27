package de.dercompiler.util;

import de.dercompiler.intermediate.selection.NodeAnnotation;
import firm.nodes.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class GraphUtil {
    public static <T, E> List<T> getPredecessors(T a, Graph<T, E> graph) {
        return graph.incomingEdgesOf(a).stream().map(graph::getEdgeSource).toList();
    }

    public static <T, E> List<T> getSuccessors(T a, Graph<T, E> graph) {
        return graph.outgoingEdgesOf(a).stream().map(graph::getEdgeTarget).toList();
    }
}
