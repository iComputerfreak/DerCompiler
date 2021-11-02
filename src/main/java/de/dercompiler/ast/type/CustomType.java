package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class CustomType extends BasicType {
    
    private final String identifier;

    public CustomType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        // We actually want to compare the String instances here, not the contents,
        // since it is faster and through the string table, we ensure that equal strings also use the same instance
        // noinspection StringEquality
        return super.syntaxEqual(other) && (other instanceof CustomType type2) && type2.identifier == this.identifier;
    }
}
