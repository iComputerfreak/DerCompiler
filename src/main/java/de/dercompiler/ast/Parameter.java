package de.dercompiler.ast;

import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
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
    private int nodeId;
    private firm.Type firmType;
    private ClassDeclaration classDeclaration;

    /**
     * Creates a new Parameter
     *
     * @param position   The source code position
     * @param type       The type of the parameter
     * @param identifier The name of the parameter
     */
    public Parameter(SourcePosition position, Type type, String identifier) {
        super(position);
        this.type = type;
        this.identifier = identifier;
        this.nodeId = -1;
    }

    /**
     * Returns the parameter type
     *
     * @return
     */
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

    public void setClassDeclaration(ClassDeclaration declaration) {
        if (declaration != null) {
            classDeclaration = declaration;
        }
    }

    @Override
    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
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

    public boolean setNodeId(int id) {
        if (nodeId > 0) return false;
        nodeId = id;
        return true;
    }

    public int getNodeId() {
        return nodeId;
    }

    public boolean isIdSet() {
        return nodeId > 0;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitParameter(this);
    }

    public List<Parameter> asList() {
        return List.of(this);
    }
}
