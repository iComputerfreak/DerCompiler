package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.FirmBlock;
import firm.nodes.Block;
import org.antlr.v4.runtime.misc.MultiMap;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;

public class MyBlockSorter implements BlockSorter {
    private List<LinkedList<FirmBlock>> chains;
    private Map<FirmBlock, BlockProperties> props;
    private SorterStage stage = SorterStage.ANNOTATION;
    private Graph<FirmBlock, DefaultWeightedEdge> graph;

    @Override
    public List<FirmBlock> sortBlocks(BasicBlockGraph graph) {
        this.chains = new ArrayList<>();
        this.props = new HashMap<>();
        this.graph = graph.getGraph();

        graph.getGraph().edgeSet().forEach(e -> graph.getGraph().setEdgeWeight(e,-1));

        BreadthFirstIterator<FirmBlock, DefaultWeightedEdge> blocks = new BreadthFirstIterator<>(graph.getGraph());
        while (blocks.hasNext()) {
            visitBlock(blocks.next());
        }
        return List.of();
    }

    public void visitBlock(FirmBlock block) {
        if (props.containsKey(block)) return;
        BlockProperties props = new BlockProperties();
        this.props.put(block, props);

        int d_in = graph.inDegreeOf(block);
        int d_out = graph.outDegreeOf(block);

        props.setDegrees(d_in, d_out);

        BlockClass bClass;
        if (d_in == 0) bClass = BlockClass.START;
        //else if (d_out == 0 && block.getJumps()) bClass = BlockClass.END;
        if (d_out > 1) bClass = BlockClass.IF_HEAD;
        else if (d_out == 1 && d_in == 1) bClass = BlockClass.STRING;

    }

    enum SorterStage { ANNOTATION }
    enum BlockClass { IF_HEAD, LOOP_HEAD, STRING, START, END, DEAD }


    private class BlockProperties {
        private BlockClass bClass;
        private int dIn, dOut;

        public void setBlockClass(BlockClass bClass) {
            this.bClass = bClass;
        }

        public BlockClass getBlockClass() {
            return bClass;
        }

        public void setDegrees(int d_in, int d_out) {
            this.dIn = d_in;
            this.dOut = d_out;
        }
    }
}
