package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.FirmBlock;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class MyBlockSorter implements BlockSorter {
    @Override
    public List<FirmBlock> sortBlocks(BasicBlockGraph graph) {
        DefaultDirectedWeightedGraph<FirmBlock, DefaultWeightedEdge> copy = new DefaultDirectedWeightedGraph<>(graph.getGraph().getVertexSupplier(),
                graph.getGraph().getEdgeSupplier());
        BasicBlockGraph orderGraph = new BasicBlockGraph(copy, graph.getMethodName());
        orderGraph.getGraph();
        return List.of();
    }
}
