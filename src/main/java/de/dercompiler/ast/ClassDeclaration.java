package de.dercompiler.ast;

import de.dercompiler.util.Utils;

import java.util.List;

public final class ClassDeclaration implements ASTNode {
    
    private final String identifier;
    private final List<ClassMember> members;

    public ClassDeclaration(String identifier, List<ClassMember> members) {
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
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof ClassDeclaration otherClass) {
            return this.identifier.equals(otherClass.identifier)
                    && Utils.syntaxEquals(this.members, otherClass.members);
        }
        return false;
    }
}
