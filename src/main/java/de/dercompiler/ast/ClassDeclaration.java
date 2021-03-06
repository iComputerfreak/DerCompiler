package de.dercompiler.ast;

import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.util.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a class declaration in MiniJava.
 * <pre>
 * {@code
 * class Foo {}
 * }
 * </pre>
 */
public final class ClassDeclaration extends ASTNode {
    
    private final String identifier;
    private final List<ClassMember> members;
    private final StringTable stringTable;

    public HashMap<String, Field> getFieldMap() {
        return fieldMap;
    }

    private final HashMap<String, Field> fieldMap;

    public HashMap<String, Method> getMethodMap() {
        return methodMap;
    }

    private final HashMap<String, Method> methodMap;




    /**
     * Creates a new ClassDeclaration
     * @param position The position of the class declaration in the source code
     * @param identifier The identifier of the class
     * @param members The member of the class
     */
    public ClassDeclaration(SourcePosition position, String identifier, List<ClassMember> members) {
        super(position);
        this.identifier = identifier;
        this.members = members;
        this.stringTable = new StringTable();

        fieldMap = new HashMap<String, Field>();
        methodMap = new HashMap<String, Method>();
    }


    /**
     * Returns the identifier of the class
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Returns the mangled identifier to use in firm
     */
    public String getMangledIdentifier() {
        return Utils.transformClassIdentifier(identifier);
    }

    /**
     * Returns all members of this class
     */
    public List<ClassMember> getMembers() {
        return members;
    }
    
    public StringTable getStringTable() {
        return stringTable;
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

    /**
     * Represents a comparator which compares two {@link ClassDeclaration}s by their identifiers
     */
    public static class Comparator implements java.util.Comparator<ClassDeclaration> {

        @Override
        public int compare(ClassDeclaration o1, ClassDeclaration o2) {
            return o1.identifier.compareTo(o2.identifier);
        }
    }

    @Override
    public String toString() {
        return "class " + identifier;
    }
}
