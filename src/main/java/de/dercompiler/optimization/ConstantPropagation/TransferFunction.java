package de.dercompiler.optimization.ConstantPropagation;

import firm.Mode;
import firm.Relation;
import firm.TargetValue;
import firm.nodes.*;

import java.util.HashMap;
import java.util.Objects;

public class TransferFunction implements ITransferFunction {
    
    private static final TargetValue UNKNOWN = TargetValue.getUnknown(); // Bottom
    private static final TargetValue BAD = TargetValue.getBad();         // Top

    private final HashMap<Node, TargetValue> targetValues;

    public TransferFunction(HashMap<Node, TargetValue> targetValues) {
        this.targetValues = targetValues;
    }

    /**
     * Returns the currently calculated TargetValue for the given node
     * @param node The node
     * @return The TargetValue that the worklist algorithm has calculated so far
     */
    private TargetValue getInternal(Node node) {
        return targetValues.getOrDefault(node, TargetValue.getUnknown());
    }

    /**
     * Checks if the given left and right sides of a binary operation are unknown or bad
     * @param left The {@link TargetValue} of the left side of the binary operation
     * @param right The {@link TargetValue} of the right side of the binary operation
     * @return TargetValue.getUnknown() if any side is unknown, TargetValue.getBad() if any side is bad,
     * or null if both sides are neither bad nor unknown
     */
    private TargetValue checkBinOp(TargetValue left, TargetValue right) {
        if (left == UNKNOWN || right == UNKNOWN) {
            return UNKNOWN;
        }
        if (left == BAD || right == BAD) {
            return BAD;
        }
        return null;
    }

    @Override
    public TargetValue getTargetValue(Add node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.add(right));
    }

    @Override
    public TargetValue getTargetValue(And node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.and(right));
    }
    
    @Override
    public TargetValue getTargetValue(Cmp node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue badOrUnknown = checkBinOp(left, right);
        if (badOrUnknown != null) {
            return badOrUnknown;
        }
        // Check what the relation is and calculate its result
        Relation relation = left.compare(right);
        boolean result = relationResult(left.asInt(), right.asInt(), relation);
        return new TargetValue(result ? 1 : 0, Mode.getBs());
    }
    
    private boolean relationResult(int left, int right, Relation relation) {
        return switch (relation) {
            case True -> true;
            case False -> false;
            case Equal -> left == right;
            case Less -> left < right;
            case Greater -> left > right;
            // Should never happen for integers (only when comparing floating point NaNs)
            case Unordered -> true;
            case LessEqual -> left <= right;
            case GreaterEqual -> left >= right;
            // != for integers
            case LessGreater -> left != right;
            // True for integers
            case LessEqualGreater -> true;
            // The unordered case in these never happens
            case UnorderedEqual -> relationResult(left, right, Relation.Equal);
            case UnorderedLess -> relationResult(left, right, Relation.Less);
            case UnorderedLessEqual -> relationResult(left, right, Relation.LessEqual);
            case UnorderedGreater -> relationResult(left, right, Relation.Greater);
            case UnorderedGreaterEqual -> relationResult(left, right, Relation.GreaterEqual);
            case UnorderedLessGreater -> relationResult(left, right, Relation.LessGreater);
        };
    }

    @Override
    public TargetValue getTargetValue(Const node) {
        return node.getTarval();
    }

    @Override
    public TargetValue getTargetValue(Div node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.div(right));
    }

    @Override
    public TargetValue getTargetValue(Eor node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.eor(right));
    }

    @Override
    public TargetValue getTargetValue(Id node) {
        return getInternal(node.getPred());
    }

    @Override
    public TargetValue getTargetValue(Minus node) {
        return getInternal(node.getPred(0)).neg();
    }

    @Override
    public TargetValue getTargetValue(Mod node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.mod(right));
    }

    @Override
    public TargetValue getTargetValue(Mul node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.mul(right));
    }

    @Override
    public TargetValue getTargetValue(Mulh node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        long leftL = left.asInt();
        long rightL = right.asInt();
        long result = leftL * rightL;
        // Only use the upper 32 bits, cut the lower 32 bits off
        result >>= 32;
        // Convert back to int
        return Objects.requireNonNullElse(checkBinOp(left, right),
                new TargetValue((int) result, Mode.getIs()));
    }

    @Override
    public TargetValue getTargetValue(Mux node) {
        TargetValue sel = getInternal(node.getSel());
        if (sel == UNKNOWN) {
            return UNKNOWN;
        }
        if (sel == BAD) {
            return BAD;
        }
        // sel == false
        if (sel.asInt() == 0) {
            return getInternal(node.getFalse());
        }
        if (sel.asInt() == 1) {
            return getInternal(node.getTrue());
        }
        // sel is not 0 or 1
        return UNKNOWN;
    }

    @Override
    public TargetValue getTargetValue(Not node) {
        return getInternal(node.getPred(1)).not();
    }

    @Override
    public TargetValue getTargetValue(Or node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.or(right));
    }

    @Override
    public TargetValue getTargetValue(Shl node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.shl(right));
    }

    @Override
    public TargetValue getTargetValue(Shr node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.shr(right));
    }

    @Override
    public TargetValue getTargetValue(Shrs node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.shrs(right));
    }

    @Override
    public TargetValue getTargetValue(Sub node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        return Objects.requireNonNullElse(checkBinOp(left, right),
                left.sub(right));
    }
}
