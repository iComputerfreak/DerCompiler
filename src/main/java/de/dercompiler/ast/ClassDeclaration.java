package de.dercompiler.ast;

import de.dercompiler.ast.expression.Variable;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
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

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        try {
            this.members.sort(new ClassMember.Comparator()::compare);
        } catch (UnsupportedOperationException e) {
            new OutputMessageHandler(MessageOrigin.AST).internalError("Tried to sort immutable list of ClassMembers.");
        }
        astNodeVisitor.visitClassDeclaration(this);
    }

    public static class Comparator implements java.util.Comparator<ClassDeclaration> {

        @Override
        public int compare(ClassDeclaration o1, ClassDeclaration o2) {
            return o1.identifier.compareTo(o2.identifier);
        }
    }
}
