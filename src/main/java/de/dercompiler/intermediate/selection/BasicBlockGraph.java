package de.dercompiler.intermediate.selection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class BasicBlockGraph {
    
    private final Graph<FirmBlock, DefaultWeightedEdge> graph;
    private final String methodName;
    
    public BasicBlockGraph(Graph<FirmBlock, DefaultWeightedEdge> graph, String methodName) {
        this.graph = graph;
        this.methodName = methodName;
    }

    public Graph<FirmBlock, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public String getMethodName() {
        return methodName;
    }
}
