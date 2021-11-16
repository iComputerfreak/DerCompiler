package de.dercompiler.ast;

import de.dercompiler.ast.expression.Variable;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.SymbolTable;
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

    private HashMap<String, SymbolTable> methodSymbolTables, fieldSymbolTables;
    private HashMap<String, StringTable> methodStringTables, fieldStringTables;

    public HashMap<String, StringTable> getMethodStringTables() {
        return methodStringTables;
    }

    public void setMethodStringTables(HashMap<String, StringTable> methodStringTables) {
        this.methodStringTables = methodStringTables;
    }

    public HashMap<String, StringTable> getFieldStringTables() {
        return fieldStringTables;
    }

    public void setFieldStringTables(HashMap<String, StringTable> fieldStringTables) {
        this.fieldStringTables = fieldStringTables;
    }

    public HashMap<String, SymbolTable> getMethodSymbolTables() {
        return methodSymbolTables;
    }

    public void setMethodSymbolTables(HashMap<String, SymbolTable> methodSymbolTables) {
        this.methodSymbolTables = methodSymbolTables;
    }

    public HashMap<String, SymbolTable> getFieldSymbolTables() {
        return fieldSymbolTables;
    }

    public void setFieldSymbolTables(HashMap<String, SymbolTable> fieldSymbolTables) {
        this.fieldSymbolTables = fieldSymbolTables;
    }



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
    }


    /**
     * Returns the identifier of the class
     */
    public String getIdentifier() {
        return identifier;
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
}
