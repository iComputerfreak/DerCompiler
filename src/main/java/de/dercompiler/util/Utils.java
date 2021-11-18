package de.dercompiler.util;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    
    public static <E1 extends ASTNode, E2 extends ASTNode> boolean syntaxEquals(List<E1> first, List<E2> second) {
        // both are null => equal
        if (first == null && second == null) {
            return true;
        }
        // only one is null => not equal
        if (first == null || second == null) {
            return false;
        }
        if (first.size() != second.size()) {
            return false;
        }
        // Iterate over the lists and compare the elements
        Iterator<E1> it1 = first.iterator();
        Iterator<E2> it2 = second.iterator();
        while (it1.hasNext()) {
            // If only one element mismatches, return false
            if (!it1.next().syntaxEquals(it2.next())) {
                return false;
            }
        }
        return true;
    }

    public static List<Variable> getReferencedVariables(Expression ex) {
        // These expressions cannot reference any variables
        if (ex instanceof ErrorExpression || ex instanceof UninitializedValue || ex instanceof VoidExpression) {
            return new LinkedList<>();
        } else if (ex instanceof BinaryExpression b) {
            // Return the variables referenced on the lhs and rhs
            List<Variable> results = getReferencedVariables(b.getLhs());
            results.addAll(getReferencedVariables(b.getRhs()));
            return results;
        } else if (ex instanceof PrimaryExpression p) {
            // These expressions cannot reference any variables
            if (p instanceof NullValue || p instanceof ThisValue || p instanceof BooleanValue
                    || p instanceof IntegerValue || p instanceof NewObjectExpression) {
                return new LinkedList<>();
            } else if (p instanceof NewArrayExpression e) {
                // NewArrayExpression has an expression in the array size that could reference variables
                return getReferencedVariables(e.getSize());
            } else if (p instanceof Variable v) {
                // If we reached a variable, we return it
                LinkedList<Variable> results = new LinkedList<>();
                results.add(v);
                return results;
            } else {
                // If we reach this statement, a new PrimaryExpression subclass has been added
                // that should be considered in the if-statements above
                throw new RuntimeException();
            }
        } else if (ex instanceof UnaryExpression u) {
            // E.g. '-a'
            return getReferencedVariables(u.getEncapsulated());
        } else {
            // If we reach this statement, a new Expression subclass has been added
            // that should be considered in the if-statements above
            throw new RuntimeException();
        }
    }
    
}
