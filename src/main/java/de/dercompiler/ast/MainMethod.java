package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a main method in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         public static void foo(String[] args) {} // This is the main method
 *     }
 *     }
 * </pre>
 */
public final class MainMethod extends ClassMember {

    private final String identifier;
    private final Type parameterType;
    private final String parameterName;
    private final MethodRest methodRest;
    private final BasicBlock block;

    /**
     * Creates a new MainMethod
     * @param position The source code position
     * @param identifier The identifier of the main method
     * @param parameterType The type of the parameter
     * @param parameterName The name of the parameter
     * @param methodRest The method rest (e.g. {@code throws}-statement) or null, if there is none
     * @param block The method block
     */
    public MainMethod(SourcePosition position, String identifier, Type parameterType, String parameterName, MethodRest methodRest, BasicBlock block) {
        super(position);
        this.identifier = identifier;
        this.parameterType = parameterType;
        this.parameterName = parameterName;
        this.methodRest = methodRest;
        this.block = block;
    }

    /**
     * Returns the name of this main method
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the type of the parameter
     */
    public Type getParameterType() {
        return parameterType;
    }

    /**
     * Returns the name of the parameter
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Returns the method rest (e.g. the {@code throws}-statement) or null, if there is none
     */
    public MethodRest getMethodRest() {
        return methodRest;
    }

    /**
     * Returns the code block of the main method
     */
    public BasicBlock getBlock() {
        return block;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof MainMethod otherMain) {
            // If this rest is null, but the other is not, return false
            if (this.methodRest == null && otherMain.methodRest != null) {
                return false;
            }
            // If this rest is not null, both rests must have equal syntax
            if (this.methodRest != null && !this.methodRest.syntaxEquals(otherMain.methodRest)) {
                return false;
            }
            return this.identifier.equals(otherMain.identifier)
                    && this.parameterType.syntaxEquals(otherMain.parameterType)
                    && this.parameterName.equals(otherMain.parameterName)
                    && this.block.syntaxEquals(otherMain.block);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitMainMethod(this);
    }
}
