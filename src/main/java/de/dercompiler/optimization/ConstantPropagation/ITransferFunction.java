package de.dercompiler.optimization.ConstantPropagation;

import firm.TargetValue;
import firm.nodes.*;

import java.util.HashMap;

public interface ITransferFunction {
    public void setTargetValues(HashMap<Node, TargetValue> targetValues);

    TargetValue getTargetValue(Add node);

    TargetValue getTargetValue(And node);

    TargetValue getTargetValue(Cmp node);

    TargetValue getTargetValue(Const node);

    TargetValue getTargetValue(Div node);

    TargetValue getTargetValue(Eor node);

    TargetValue getTargetValue(Id node);

    TargetValue getTargetValue(Minus node);

    TargetValue getTargetValue(Mod node);

    TargetValue getTargetValue(Mul node);

    TargetValue getTargetValue(Mulh node);

    TargetValue getTargetValue(Mux node);

    TargetValue getTargetValue(Not node);

    TargetValue getTargetValue(Phi node);

    TargetValue getTargetValue(Or node);

    TargetValue getTargetValue(Shl node);

    TargetValue getTargetValue(Shr node);

    TargetValue getTargetValue(Shrs node);

    TargetValue getTargetValue(Sub node);
}
