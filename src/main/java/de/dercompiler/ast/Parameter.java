package de.dercompiler.ast;

import de.dercompiler.ast.expression.ASTDefinition;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

import java.util.List;

/**
 * Represents a parameter in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         //                 This is the parameter
 *         //                     V --------- V
 *         public static void foo(String[] args) throws NullPointerException {}
 *     }
 *     }
 * </pre>
 */
public final class Parameter extends ASTNode implements ASTDefinition {

    private final Type type;
    private final String identifier;

    /**
     * Creates a new Parameter
     * @param position The source code position
     * @param type The type of the parameter
     * @param identifier The name of the parameter
     */
    public Parameter(SourcePosition position, Type type, String identifier) {
        super(position);
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * Returns the parameter type
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the parameter name
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Parameter otherParam) {
            return this.type.syntaxEquals(otherParam.type)
                    && this.identifier.equals(otherParam.identifier);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitParameter(this);
    }

    public List<Parameter> asList() {
        return List.of(this);
    }
}
