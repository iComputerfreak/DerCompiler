package de.dercompiler.optimization;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Construction;
import firm.Graph;
import firm.Mode;
import firm.nodes.*;

import java.util.HashMap;
import java.util.function.BiFunction;

public class ArithmeticOptimization extends GraphOptimization {

    private final HashMap<Integer, ReplaceDivNode> divNodesData;
    private OutputMessageHandler logger;

    public ArithmeticOptimization() {
        this.divNodesData = new HashMap<>();
        this.logger = new OutputMessageHandler(MessageOrigin.TRANSFORM);
    }

    @Override
    public void visit(Add node) {
        Node summand1 = node.getLeft();
        Node summand2 = node.getRight();
        boolean success = symmetric(summand1, summand2, (a, b) -> {
            // 0 + x -> x
            if (a instanceof Const constA && constA.getTarval().asInt() == 0) {
                replaceNode(node, b);
                logger.printInfo("Apply arith/AddId to %s and %s".formatted(a.toString(), b.toString()));
                return true;
            } else if (a instanceof Minus negativeNode) {
                replaceNode(node, getConstruction().newSub(b, negativeNode.getPred(0)));
                logger.printInfo("Apply arith/AddToSub to %s and %s".formatted(a.toString(), b.toString()));
                return true;
            }
            return false;
        });

        if (success) return;

        if (summand1 == summand2) {
            // x + x -> x << 1
            Construction construction = getConstruction();
            replaceNode(node, construction.newShl(summand1, construction.newConst(1, Mode.getIu())));
            logger.printInfo("Apply arith/AddSameArg to %s and %s".formatted(summand1.toString(), summand2.toString()));
            return;
        }
    }

    @Override
    public void visit(Sub node) {
        Node minuend = node.getLeft();
        Node subtrahend = node.getRight();
        Construction constr = getConstruction();

        if (minuend instanceof Const mndConst && mndConst.getTarval().asInt() == 0) {
            // 0 - x -> -x
            replaceNode(node, constr.newMinus(subtrahend));
            logger.printInfo("Apply arith/SubToNeg to %s and %s".formatted(minuend.toString(), subtrahend.toString()));
        } else if (subtrahend instanceof Const sbtConst && sbtConst.getTarval().asInt() == 0) {
            // x - 0 -> x
            replaceNode(node, minuend);
            logger.printInfo("Apply arith/SubId to %s and %s".formatted(minuend.toString(), subtrahend.toString()));
        } else if (minuend == subtrahend) {
            // x - x -> 0
            replaceNode(node, constr.newConst(0, Mode.getIs()));
            logger.printInfo("Apply arith/SubSameArg to %s and %s".formatted(minuend.toString(), subtrahend.toString()));
        } else if (subtrahend instanceof Minus negativeNode) {
            // x - -y -> x + y
            replaceNode(node, constr.newAdd(minuend, negativeNode.getPred(0)));
            logger.printInfo("Apply arith/SubToAdd to %s and %s".formatted(minuend.toString(), subtrahend.toString()));
        } else if (minuend instanceof Minus negativeNode) {
            // -x - y -> -(x + y)
            Node add = constr.newAdd(negativeNode.getPred(0), subtrahend);
            logger.printInfo("Apply arith/SubFirstNeg to %s and %s".formatted(minuend.toString(), subtrahend.toString()));
            add.accept(this);
            replaceNode(node, constr.newMinus(add));
        }
    }


    @Override
    public void visit(Mul node) {
        symmetric(node.getLeft(), node.getRight(), (a, b) -> {
            if (a instanceof Const constA) {
                int aValue = constA.getTarval().asInt();
                return switch (aValue) {
                    // 0 * x -> 0
                    case 0 -> {
                        replaceNode(node, a);
                        logger.printInfo("Apply arith/MulWithZero to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }

                    // 1 * x -> x
                    case 1 -> {
                        replaceNode(node, b);
                        logger.printInfo("Apply arith/MulId to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }
                    default -> {
                        // if non-negative, aValue is exactly a power of two
                        int exponent = getHighestOneBit(aValue);

                        // (2 ** exp) * x -> x << exp
                        if (exponent > 0) {
                            replaceNode(node, getConstruction().newShl(b, getConstruction().newConst(exponent, Mode.getIu())));
                            logger.printInfo("Apply arith/MulToLsh to %s and %s".formatted(a.toString(), b.toString()));
                            yield true;
                        }
                        yield false;
                    }
                };
            } else return false;
        });
    }

    @Override
    public void visit(Div node) {
        Node dividend = node.getLeft();
        Node divisor = node.getRight();

        // case 'dividend == 0' cannot be taken advantage from unless divisor is constant as well and _not_ 0, in which case other optimizations apply.
        if (divisor instanceof Const dsrConst) {
            int dsrValue = dsrConst.getTarval().asInt();
            switch (dsrValue) {

                // x / -1 -> -x
                case -1 -> {
                    replaceDiv(node, getConstruction().newMinus(dividend));
                    logger.printInfo("Apply arith/DivByMinusOne to %s and %s".formatted(dividend.toString(), divisor.toString()));
                }

                // x / 1 -> x
                case 1 -> {
                    replaceDiv(node, dividend);
                    logger.printInfo("Apply arith/DivId to %s and %s".formatted(dividend.toString(), divisor.toString()));
                }

                default -> {
                    // if non-negative, dsrValue is exactly a power of two
                    int exponent = getHighestOneBit(dsrValue);

                    // x / (2 ** exp) -> x >> exp
                    if (exponent > 0) {
                        replaceDiv(node, getConstruction().newShrs(dividend, getConstruction().newConst(exponent, Mode.getIu())));
                    logger.printInfo("Apply arith/DivToRshs to %s and %s".formatted(dividend.toString(), divisor.toString()));
                    }
                }
            }

        }
    }

    private void replaceDiv(Div divNode, Node newNode) {
        // Must replace by hand, or else the Proj nodes' predecessors will have invalid types.
        this.divNodesData.put(divNode.getNr(), new ReplaceDivNode(newNode, divNode.getMem()));
    }

    @Override
    public void visit(Mod node) {
        Node dividend = node.getLeft();
        Node divisor = node.getRight();

        // case 'dividend == 0' cannot be taken advantage from unless divisor is constant as well and _not_ 0, in which case other optimizations apply.

        // not sure what kind of optimization would be applicable, because the result depends on the sign of the dividend
    }

    @Override
    public void visit(Or node) {
        symmetric(node.getLeft(), node.getRight(), (a, b) -> {
            if (a instanceof Const constA) {
                return switch (constA.getTarval().asInt()) {
                    // -1 || x -> -1
                    case -1 -> {
                        replaceNode(node, a);
                        logger.printInfo("Apply arith/OrMinusOne to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }

                    // 0 || x -> x
                    case 0 -> {
                        replaceNode(node, b);
                        logger.printInfo("Apply arith/OrId to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }
                    default -> false;
                };
            } else return false;
        });
    }

    @Override
    public void visit(And node) {
        symmetric(node.getLeft(), node.getRight(), (a, b) -> {
            if (a instanceof Const constA) {
                return switch (constA.getTarval().asInt()) {
                    // -1 && x -> x
                    case -1 -> {
                        replaceNode(node, b);
                        logger.printInfo("Apply arith/AndId to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }

                    // 0 && x -> 0
                    case 0 -> {
                        replaceNode(node, a);
                        logger.printInfo("Apply arith/AndZero to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }
                    default -> false;
                };
            } else return false;
        });
    }

    @Override
    public void visit(Eor node) {
        symmetric(node.getLeft(), node.getRight(), (a, b) -> {
            if (a instanceof Const constA) {
                return switch (constA.getTarval().asInt()) {
                    // -1 ^ x -> ~x
                    case -1 -> {
                        replaceNode(node, getConstruction().newNot(b));
                        logger.printInfo("Apply arith/XorMinusOne to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }

                    // 0 ^ x -> x
                    case 0 -> {
                        replaceNode(node, b);
                        logger.printInfo("Apply arith/XorId to %s and %s".formatted(a.toString(), b.toString()));
                        yield true;
                    }
                    default -> false;
                };
            } else return false;
        });
    }

    @Override
    public void visit(Shl node) {
        Node base = node.getLeft();
        Node shift = node.getRight();

        if (shift instanceof Const shConst && shConst.getTarval().asInt() == 0) {
            // x << 0 -> x
            replaceNode(node, base);
            logger.printInfo("Apply arith/LshId to %s and %s".formatted(base.toString(), shift.toString()));
        }
    }

    @Override
    public void visit(Shr node) {
        Node base = node.getLeft();
        Node shift = node.getRight();

        if (shift instanceof Const shConst && shConst.getTarval().asInt() == 0) {
            // x >> 0 -> x
            replaceNode(node, base);
            logger.printInfo("Apply arith/RshId to %s and %s".formatted(base.toString(), shift.toString()));
        }
    }

    @Override
    public void visit(Shrs node) {
        Node base = node.getLeft();
        Node shift = node.getRight();

        if (shift instanceof Const shConst && shConst.getTarval().asInt() == 0) {
            // x >> 0 -> x
            replaceNode(node, base);
            logger.printInfo("Apply arith/RshsId to %s and %s".formatted(base.toString(), shift.toString()));
        }
    }

    @Override
    public void visit(Minus node) {
        Node pred = node.getPred(0);
        if (pred instanceof Minus innerNegNode) {
            // -(-x) -> x
            replaceNode(node, innerNegNode.getPred(0));
            logger.printInfo("Apply arith/MinusMinus to %s and %s".formatted(node.toString(), pred.toString()));
        }
    }

    @Override
    public void visit(Proj node) {
        if (node.getPred() instanceof Div divNode && divNodesData.containsKey(divNode.getNr())) {
            final ReplaceDivNode data = divNodesData.get(divNode.getNr());
            switch (node.getMode().getName()) {
                case "M" -> replaceNode(node, data.memory);
                case "Is" -> replaceNode(node, data.replaceValue);
                default -> logger.internalError("A Proj node with a Mode different from M or Is seems to have pointed to an Integer Div node. How is that supposed to happen?");
            }
        }
    }

    private void replaceNode(Node node, Node b) {
        Graph.exchange(node, b);
        b.accept(this);
    }

    private static int getHighestOneBit(int value) {
        int exponent = 0;
        if (value == 0) return -1;
        while ((value & 1) == 0) {
            // zero bit is not set
            value >>= 1;
            exponent++;
        }
        // zero bit is set. If value was power of two, it is now exactly one.
        if (value == 1) return exponent;

        // not a power of two.
        return -1;
    }

    private boolean symmetric(Node dividend, Node divisor, BiFunction<Node, Node, Boolean> biFunction) {
        boolean success = biFunction.apply(dividend, divisor);
        success = success || biFunction.apply(divisor, dividend);  // lazy
        return success;
    }

    record ReplaceDivNode(Node replaceValue, Node memory) {

    }

    ;


}
