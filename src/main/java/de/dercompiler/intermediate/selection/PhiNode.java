package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.rules.PhiRule;

import java.util.ArrayList;
import java.util.List;

public class PhiNode extends CodeNode {

    private final List<CodeNode> predCode;

    public PhiNode(PhiRule rule, FirmBlock block) {
        super(new ArrayList<>(), block, rule.node.getNr());
        predCode = new ArrayList<>();
    }

    public void setCodeForPred(CodeNode codeForPred, int i) {
        if (predCode.size() == i) {
            predCode.add(codeForPred);
        } else {
            this.predCode.set(i, codeForPred);
        }
    }

    public CodeNode getCodeForPred(int i) {
        return predCode.get(i);
    }

    @Override
    public boolean isPhi() {
        return true;
    }

    public int getPredCount() {
        return predCode.size();
    }
}
