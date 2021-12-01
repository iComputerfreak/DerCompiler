package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public sealed class Method extends ClassMember permits MainMethod {

    private final Type type;
    private final String identifier;
    private final List<Parameter> parameters;
    private final MethodRest methodRest;
    private final BasicBlock block;
    private ClassDeclaration surrounding;

    private int numLocalVars;

    /**
     * Creates a new Method
     * @param position The source code position
     * @param type The return type
     * @param identifier The name of the method
     * @param parameters The parameters
     * @param methodRest The method rest (e.g. {@code throws}-statement) or null, if there is none
     * @param block The code block
     */
    public Method(SourcePosition position, Type type, String identifier, List<Parameter> parameters, MethodRest methodRest, BasicBlock block) {
        super(position);
        this.type = type;
        this.identifier = identifier;
        this.parameters = Objects.requireNonNullElseGet(parameters, LinkedList::new);
        this.methodRest = methodRest;
        this.block = block;
        this.numLocalVars = -1;
    }

    public boolean isStatic() { return false; }

    public Type getType() {
        return type;
    }

    /**
     * Returns the name of the method
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the parameters of this method or an empty list, if there are none
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Returns the method rest (e.g. {@code throws}-statement) or null, if there is none
     */
    public MethodRest getMethodRest() {
        return methodRest;
    }

    /**
     * Returns the code block of this method
     */
    public BasicBlock getBlock() {
        return block;
    }

    protected boolean internalEquals(Method otherMethod) {
        if (this.methodRest == null && otherMethod.methodRest != null) {
            return false;
        }
        // If this rest is not null, both rests must have equal syntax
        if (this.methodRest != null && !this.methodRest.syntaxEquals(otherMethod.methodRest)) {
            return false;
        }
        return this.type.syntaxEquals(otherMethod.type)
                && this.identifier.equals(otherMethod.identifier)
                && Utils.syntaxEquals(this.parameters, otherMethod.parameters)
                && this.block.syntaxEquals(otherMethod.block);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Method otherMethod) {
            if (otherMethod instanceof MainMethod) return false;
            // If this rest is null, but the other is not, return false
            return internalEquals(otherMethod);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitMethod(this);
    }

    public void setSurroundingClass(ClassDeclaration declaration) {
        this.surrounding = declaration;
    }

    public ClassDeclaration getSurroundingClass() {
        return surrounding;
    }

    public int getNumLocalVariables() {
        return numLocalVars;
    }

    public void setNumLocalVariables(int numLocalVars) {
        this.numLocalVars = numLocalVars;
    }

    @Override
    public String toString() {
        if (getSurroundingClass() == null) return "Method " + getIdentifier();
        return "%s::%s".formatted(getSurroundingClass().getIdentifier(), getIdentifier());
    }
}
