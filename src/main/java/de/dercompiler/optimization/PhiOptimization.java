package de.dercompiler.optimization;

import firm.Graph;
import firm.Mode;
import firm.nodes.Const;
import firm.nodes.Node;
import firm.nodes.Phi;

import java.util.HashMap;
import java.util.Map;

public class PhiOptimization extends GraphOptimization {

    Map<Integer, Node> constants;

    public PhiOptimization() {
        this.constants = new HashMap<>();
    }

    @Override
    public void visit(Const node) {

        int value = node.getTarval().asInt();
        if (constants.containsKey(value)) {
            Node constant = constants.get(value);
            Graph.exchange(node, constant);
        } else {
            constants.put(value, node);
        }
    }

    @Override
    public void visit(Phi node) {
        if (node.getMode() == Mode.getM()) return;

        Node pred0 = node.getPred(0);
        Node pred1 = node.getPred(1);
        if (pred0.equals(pred1)) {
            Graph.exchange(node, pred0);
        }
    }


}
