package de.dercompiler.optimization.ConstantPropagation;

import firm.Mode;
import firm.Relation;
import firm.TargetValue;
import firm.nodes.*;

import java.util.Map;
import java.util.Objects;

public class TransferFunction implements ITransferFunction {

    private static final TargetValue UNKNOWN = Worklist.UNKNOWN; // Bottom
    private static final TargetValue BAD = Worklist.BAD;         // Top

    private Map<Integer, TargetValue> targetValues;

    public void setTargetValues(Map<Integer, TargetValue> targetValues) {
        this.targetValues = targetValues;
    }


    /**
     * Returns the currently calculated TargetValue for the given node
     *
     * @param node The node
     * @return The TargetValue that the worklist algorithm has calculated so far
     */
    private TargetValue getInternal(Node node) {
        return targetValues.getOrDefault(node.getNr(), UNKNOWN);
    }

    /**
     * Checks if the given left and right sides of a binary operation are unknown or bad
     *
     * @param left  The {@link TargetValue} of the left side of the binary operation
     * @param right The {@link TargetValue} of the right side of the binary operation
     * @return TargetValue.getUnknown() if any side is unknown, TargetValue.getBad() if any side is bad,
     * or null if both sides are neither bad nor unknown
     */
    private TargetValue checkBinOp(TargetValue left, TargetValue right) {
        if (left.equals(UNKNOWN) || right.equals(UNKNOWN)) {
            return UNKNOWN;
        }
        if (left.equals(BAD) || right.equals(BAD)) {
            return BAD;
        }
        return null;
    }

    private TargetValue checkUnaryOp(TargetValue inner) {
        if (inner.equals(UNKNOWN)) {
            return UNKNOWN;
        }
        if (inner.equals(BAD)) {
            return BAD;
        }
        return null;
    }

    private boolean areEqual(Node n1, TargetValue t2) {
        return areEqual(getInternal(n1), t2);
    }

    private boolean areEqual(TargetValue t1, Node n2) {
        return areEqual(getInternal(n2), t1);
    }

    private boolean areEqual(Node n1, Node n2) {
        return areEqual(getInternal(n1), getInternal(n2));
    }

    private boolean areEqual(TargetValue t1, TargetValue t2) {
        if (t1.equals(UNKNOWN)) {
            return t2.equals(UNKNOWN);
        }
        if (t1.equals(TargetValue.getBad())) {
            return t2.equals(TargetValue.getBad());
        }
        return t1.asInt() == t2.asInt();
    }

    private boolean isTopOrBottom(Node n) {
        return isTopOrBottom(getInternal(n));
    }

    private boolean isTopOrBottom(TargetValue t) {
        return t.equals(BAD) || t.equals(UNKNOWN);
    }

    @Override
    public TargetValue getTargetValue(Add node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.add(right) : value;
    }

    @Override
    public TargetValue getTargetValue(And node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.and(right) : value;
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
        boolean result = node.getRelation().contains(relation);
        return result ? TargetValue.getBTrue() : TargetValue.getBFalse();
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
    public TargetValue getTargetValue(Conv node) {
        return getInternal(node.getOp());
    }


    @Override
    public TargetValue getTargetValue(Div node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());

        TargetValue value = checkBinOp(left, right);
        return value == null ?
                new TargetValue(left.asInt() / right.asInt(), left.getMode()) : value;
    }

    @Override
    public TargetValue getTargetValue(Eor node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.eor(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Phi node) {
        TargetValue actual = getInternal(node);

        for (Node pred : node.getPreds()) {
            if (areEqual(actual, UNKNOWN)) {
                actual = getInternal(pred);
            } else if (areEqual(actual, BAD) || areEqual(pred, BAD)) {
                actual = BAD;
            } else if (!areEqual(pred, UNKNOWN) && !areEqual(actual, pred)) {
                actual = BAD;
            }

        }

        return actual;
    }

    @Override
    public TargetValue getTargetValue(Id node) {
        return getInternal(node.getPred());
    }

    @Override
    public TargetValue getTargetValue(Minus node) {
        TargetValue inner = getInternal(node.getPred(0));
        return checkUnaryOp(inner) == null ? inner.neg() : inner;
    }

    @Override
    public TargetValue getTargetValue(Mod node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.mod(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Mul node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.mul(right) : value;
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
        TargetValue value = checkBinOp(left, right);
        return value == null ? new TargetValue((int) result, Mode.getIs()) : value;
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
        TargetValue inner = getInternal(node.getPred(0));
        return checkUnaryOp(inner) == null ? inner.not() : inner;
    }

    @Override
    public TargetValue getTargetValue(Or node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.or(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Shl node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.shl(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Shr node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.shr(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Shrs node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.shrs(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Sub node) {
        TargetValue left = getInternal(node.getLeft());
        TargetValue right = getInternal(node.getRight());
        TargetValue value = checkBinOp(left, right);
        return value == null ? left.sub(right) : value;
    }

    @Override
    public TargetValue getTargetValue(Proj node) {
        return Objects.equals(node.getMode(), Mode.getM()) ? BAD : getInternal(node.getPred());
    }

    @Override
    public TargetValue getTargetValue(Load node) {
        return BAD;
    }
}
