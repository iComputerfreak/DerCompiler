package de.dercompiler;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.SymbolTable;
import de.dercompiler.util.Utils;

import java.util.LinkedList;
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

    private boolean isIndexed;
    private final SymbolTable symbolTable;
    private final GlobalScope globalScope;
    private final List<Function> functions;
    private final StringTable stringTable;


    /**
     * Creates a new Program
     * @param position The source code position
     * @param classes The list of classes in this program
     */
    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        super(position);
        this.classes = classes;
        this.symbolTable = new SymbolTable();
        this.stringTable = new StringTable();
        this.globalScope = new GlobalScope();
        this.functions = new LinkedList<>();
        isIndexed = false;
    }

    /**
     * Returns all classes in this program
     */
    public List<ClassDeclaration> getClasses() {
        return classes;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
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

    public boolean isIndexed() {
        return isIndexed;
    }

    public void indexed() {
        isIndexed = true;
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }
    
    public List<Function> getFunctions() {
        return functions;
    }

    public StringTable getStringTable() {
        return stringTable;
    }
}
