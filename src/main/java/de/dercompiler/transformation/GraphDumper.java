package de.dercompiler.transformation;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.CodeNode;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Dump;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    public static <E> void dumpNodeAnnotationGraph(Graph<NodeAnnotation, E> graph, String name, Function<firm.nodes.Node, NodeAnnotation> annotationSupplier) {
        dumpJGraph(graph, "annotationGraph", name, v -> Integer.toString(v.getRootNode().getNr()), (v) -> {
            SubstitutionRule rule = v.getRule();
            rule.setAnnotations(v, annotationSupplier);
            List<Operation> ops = rule.substitute();
            rule.clearAnnotations();
            List<String> opStrings = ops.stream().map(o -> o.getOperationType().toString()).toList();
            String label = "Cost: " + v.getRule().getCost() + "\n" +
                    v.getRootNode().toString() +
                    "\n-----\n" +
                    String.join("\n", opStrings);
            
            return Map.of("label", DefaultAttribute.createAttribute(label));
        }, (e) -> new HashMap<>());
    }

    public static <E> void dumpCodeGraph(Graph<CodeNode, E> graph, String name) {
        dumpJGraph(graph, "codeGraph", name, v -> Integer.toString(v.getId()), (v) -> {
            List<Operation> ops = v.getOperations();
            List<String> opStrings = ops.stream().map(o -> o.getOperationType().toString()).toList();
            return Map.of("label", DefaultAttribute.createAttribute(v.getId() + "\n" + String.join("\n", opStrings)));
        }, (e) -> new HashMap<>());
    }

    public static <E> void dumpBlocksGraph(Graph<FirmBlock, E> graph, String name) {
        dumpJGraph(graph, "blocksGraph", name, v -> Integer.toString(v.getNr()),
                (v) -> Map.of("label", DefaultAttribute.createAttribute(v.toString() + "\n" + 
                        String.join("\n", v.getOperations().stream().map(o -> o.getOperationType().toString()).toList()))),
                (e) -> Map.of("label", DefaultAttribute.createAttribute((int) graph.getEdgeWeight(e))));
    }
    
    private static <V, E> void dumpJGraph(Graph<V, E> graph, String namePrefix, String name,
                                          Function<V, String> vertexIdProvider,
                                          Function<V, Map<String, Attribute>> vertexAttributeProvider,
                                          Function<E, Map<String, Attribute>> edgeAttributeProvider) {
        if (dump_graph) {
            String file = namePrefix + "-" + name + ".dot";
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Dumped graph: " + file);
            DOTExporter<V, E> exporter = new DOTExporter<>(vertexIdProvider);
            exporter.setVertexAttributeProvider(vertexAttributeProvider);
            exporter.setEdgeAttributeProvider(edgeAttributeProvider);
            
            File directory = new File("graphs");
            directory.mkdir();
            try(FileWriter w = new FileWriter("graphs/" + file)) {
                exporter.exportGraph(graph, w);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dump(boolean active) {
        dump_graph = active;
    }
}
