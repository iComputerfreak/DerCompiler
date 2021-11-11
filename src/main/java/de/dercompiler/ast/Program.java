package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

/**
 * Represents a program in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         public int a;
 *         public static void foo(String[] args) throws NullPointerException {}
 *         public void bar() {
 *             this.a = 0;
 *         }
 *     }
 *     
 *     class Bar {}
 *     }
 * </pre>
 */
public final class Program extends ASTNode {

    private final List<ClassDeclaration> classes;

    /**
     * Creates a new Program
     * @param position The source code position
     * @param classes The list of classes in this program
     */
    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        super(position);
        this.classes = classes;
    }

    /**
     * Returns all classes in this program
     */
    public List<ClassDeclaration> getClasses() {
        return classes;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Program otherProgram) {
            return Utils.syntaxEquals(this.classes, otherProgram.classes);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        try {
            this.classes.sort(new ClassDeclaration.Comparator()::compare);
        } catch (UnsupportedOperationException e) {
            new OutputMessageHandler(MessageOrigin.AST).internalError("Tried to sort immutable list of ClassDeclarations.");
        }
        astNodeVisitor.visitProgram(this);
    }
}
