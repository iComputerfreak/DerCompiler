package de.dercompiler.util;

import de.dercompiler.ast.ASTNode;

import java.util.Iterator;
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
    
}
