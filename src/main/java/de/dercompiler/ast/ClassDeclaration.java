package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class ClassDeclaration extends ASTNode {
    
    private final String identifier;
    private final List<ClassMember> members;

    public ClassDeclaration(SourcePosition position, String identifier, List<ClassMember> members) {
        super(position);
        this.identifier = identifier;
        this.members = members;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<ClassMember> getMembers() {
        return members;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof ClassDeclaration otherClass) {
            return this.identifier.equals(otherClass.identifier)
                    && Utils.syntaxEquals(this.members, otherClass.members);
        }
        return false;
    }
}
