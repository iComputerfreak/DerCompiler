package de.dercompiler.ast;

import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

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
public final class Field extends ClassMember implements ASTDefinition {

    private final Type type;
    private final String identifier;
    private firm.Type firmType;

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

    public Type getType() {
        return type;
    }
    
    public void setFirmType(firm.Type firmType) {
        this.firmType = firmType;
    }

    @Override
    public firm.Type getFirmType() {
        return firmType;
    }

    /**
     * Returns the identifier of the field
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the mangled identifier to use in firm
     */
    public String getMangledIdentifier() {
        return Utils.transformVariableIdentifier(identifier);
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
