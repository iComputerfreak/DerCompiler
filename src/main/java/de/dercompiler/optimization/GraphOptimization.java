package de.dercompiler.optimization;

import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.transformation.LazyFirmNodeVisitor;
import de.dercompiler.transformation.TransformationState;
import firm.Construction;
import firm.Graph;
import firm.nodes.NodeVisitor;

public abstract class GraphOptimization extends LazyFirmNodeVisitor {

    private OptimizationContext context;

    public GraphOptimization() {
        this.context = new OptimizationContext();
    }

    public void runOnGraph(Graph graph) {
        this.context.setGraph(graph);
        graph.walkTopological(this);
    }

    public OptimizationContext getContext() {
        return context;
    }

    Construction getConstruction() {
        return context.getConstruction();
    }

    static class OptimizationContext {
        private Graph currGraph;
        private Construction construction;

        public void setGraph(Graph currGraph) {
            this.currGraph = currGraph;
            this.construction = new Construction(currGraph);
        }

        public Graph getGraph() {
            return currGraph;
        }

        public Construction getConstruction() {
            return construction;
        }
    }
}
