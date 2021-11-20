package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public final class Parameter extends ASTNode {

    private final Type type;
    private final String identifier;
    
    public Parameter(SourcePosition position, Type type, String identifier) {
        super(position);
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

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

    public List<Parameter> asList() {
        return List.of(this);
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitParameter(this);
    }
}
