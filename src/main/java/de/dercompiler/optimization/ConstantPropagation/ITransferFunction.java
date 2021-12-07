package de.dercompiler.optimization.ConstantPropagation;

import firm.TargetValue;
import firm.nodes.*;

public interface ITransferFunction {
    
    TargetValue getTargetValue(Add node);

    TargetValue getTargetValue(Address node);

    TargetValue getTargetValue(Align node);

    TargetValue getTargetValue(Alloc node);

    TargetValue getTargetValue(Anchor node);

    TargetValue getTargetValue(And node);

    TargetValue getTargetValue(Bad node);

    TargetValue getTargetValue(Bitcast node);

    TargetValue getTargetValue(Block node);

    TargetValue getTargetValue(Builtin node);

    TargetValue getTargetValue(Call node);

    TargetValue getTargetValue(Cmp node);

    TargetValue getTargetValue(Cond node);

    TargetValue getTargetValue(Confirm node);

    TargetValue getTargetValue(Const node);

    TargetValue getTargetValue(Conv node);

    TargetValue getTargetValue(CopyB node);

    TargetValue getTargetValue(Deleted node);

    TargetValue getTargetValue(Div node);

    TargetValue getTargetValue(Dummy node);

    TargetValue getTargetValue(End node);

    TargetValue getTargetValue(Eor node);

    TargetValue getTargetValue(Free node);

    TargetValue getTargetValue(IJmp node);

    TargetValue getTargetValue(Id node);

    TargetValue getTargetValue(Jmp node);

    TargetValue getTargetValue(Load node);

    TargetValue getTargetValue(Member node);

    TargetValue getTargetValue(Minus node);

    TargetValue getTargetValue(Mod node);

    TargetValue getTargetValue(Mul node);

    TargetValue getTargetValue(Mulh node);

    TargetValue getTargetValue(Mux node);

    TargetValue getTargetValue(NoMem node);

    TargetValue getTargetValue(Not node);

    TargetValue getTargetValue(Offset node);

    TargetValue getTargetValue(Or node);

    TargetValue getTargetValue(Phi node);

    TargetValue getTargetValue(Pin node);

    TargetValue getTargetValue(Proj node);

    TargetValue getTargetValue(Raise node);

    TargetValue getTargetValue(Return node);

    TargetValue getTargetValue(Sel node);

    TargetValue getTargetValue(Shl node);

    TargetValue getTargetValue(Shr node);

    TargetValue getTargetValue(Shrs node);

    TargetValue getTargetValue(Size node);

    TargetValue getTargetValue(Start node);

    TargetValue getTargetValue(Store node);

    TargetValue getTargetValue(Sub node);

    TargetValue getTargetValue(Switch node);

    TargetValue getTargetValue(Sync node);

    TargetValue getTargetValue(Tuple node);

    TargetValue getTargetValue(Unknown node);

    TargetValue getTargetValueUnknown(Node node);
}
