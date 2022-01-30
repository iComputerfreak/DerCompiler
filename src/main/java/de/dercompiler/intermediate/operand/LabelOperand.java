package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.FirmBlock;

/**
 * Represents a jump or call target.
 */
public class LabelOperand implements Operand {

    private final String target;

    public LabelOperand(String target) {
        this.target = target;
    }

    @Override
    public String getIdentifier() {
        return target;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    public LabelOperand getMainNode() {
        if (this.target.contains("_")) {
            return new LabelOperand(target.substring(0, target.indexOf("_")));
        }
        return this;
    }

    public boolean isPhiNode() {
        return this.target.contains("_");
    }
}
