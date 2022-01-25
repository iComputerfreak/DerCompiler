package de.dercompiler.intermediate.operand;

import firm.Mode;

public class CondTarget implements Operand {

    private LabelOperand trueTarget;
    private LabelOperand falseTarget;

    public CondTarget(LabelOperand trueTarget, LabelOperand falseTarget) {
        this.trueTarget = trueTarget;
        this.falseTarget = falseTarget;
    }

    public CondTarget() {}

    public void setTrueTarget(LabelOperand trueTarget) {
        this.trueTarget = trueTarget;
    }

    public void setFalseTarget(LabelOperand falseTarget) {
        this.falseTarget = falseTarget;
    }

    public LabelOperand getTrueTarget() {
        return trueTarget;
    }

    public LabelOperand getFalseTarget() {
        return falseTarget;
    }

    @Override
    public String getIdentifier() {
        return "cond ? %s : %s".formatted(trueTarget.getIdentifier(), falseTarget.getIdentifier());
    }

    @Override
    public Mode getMode() {
        return Mode.getX();
    }

    @Override
    public void setMode(Mode mode) {

    }
}
