package de.dercompiler.transformation;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.CodeNode;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Dump;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static void dumpNodeAnnotationGraph(Graph<NodeAnnotation, DefaultEdge> graph, String name) {
        if (dump_graph) {
            String file = "annotationGraph-" + name + ".dot";
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Dumped graph: " + file);
            DOTExporter<NodeAnnotation, DefaultEdge> exporter = new DOTExporter<>(v -> Integer.toString(v.getRootNode().getNr()));
            exporter.setVertexAttributeProvider((v) -> {
                List<Operation> ops = v.getRule().substitute(v.getRootNode());
                List<String> opStrings = ops.stream().map(o -> o.getOperationType().toString()).toList();
                String label = "Cost: " + v.getRule().getCost() + "\n" + v.getRootNode().toString() + "\n-----\n" + String.join("\n", opStrings);

                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(label));
                return map;
            });

            try(FileWriter w = new FileWriter(file)) {
                exporter.exportGraph(graph, w);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dumpCodeGraph(Graph<CodeNode, DefaultEdge> graph, String name) {
        if (dump_graph) {
            String file = "codeGraph-" + name + ".dot";
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Dumped graph: " + file);
            DOTExporter<CodeNode, DefaultEdge> exporter = new DOTExporter<>(v -> Integer.toString(v.getId()));
            exporter.setVertexAttributeProvider((v) -> {
                List<Operation> ops = v.getOperations();
                List<String> opStrings = ops.stream().map(o -> o.getOperationType().toString()).toList();
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(String.join("\n", opStrings)));
                return map;
            });

            try(FileWriter w = new FileWriter(file)) {
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
