package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a class field in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         public int a; // This is the field
 *     }
 *     }
 * </pre>
 */
public final class Field extends ClassMember {

    private final Type type;
    private final String identifier;

    /**
     * Creates a new Field
     * @param position The source code position
     * @param type The type of the field
     * @param identifier The name of the field
     */
    public Field(SourcePosition position, Type type, String identifier) {
        super(position);
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * Returns the type of the field
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the identifier of the field
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Field otherField) {
            return this.type.syntaxEquals(otherField.type)
                    && this.identifier.equals(otherField.identifier);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitField(this);
    }
}
