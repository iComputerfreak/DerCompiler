package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a method rest in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         //                                      This is the method rest
 *         //                                    V ----------------------- V
 *         public static void foo(String[] args) throws NullPointerException {}
 *     }
 *     }
 * </pre>
 */
public final class MethodRest extends ASTNode {

    private final String identifier;

    /**
     * Creates a new MethodRest
     * @param position The source code position
     * @param identifier The identifier of the thrown {@link Exception}
     */
    public MethodRest(SourcePosition position, String identifier) {
        super(position);
        this.identifier = identifier;
    }

    /**
     * Returns the identifier of the thrown {@link Exception}
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof MethodRest otherRest) {
            return this.identifier.equals(otherRest.identifier);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitMethodRest(this);
    }
}
