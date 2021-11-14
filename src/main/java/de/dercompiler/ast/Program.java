package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Program extends ASTNode {

    private final List<ClassDeclaration> classes;
    private boolean isIndexed;

    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        super(position);
        this.classes = classes;
        isIndexed = false;
    }

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

    public boolean isIndexed() {
        return isIndexed;
    }

    public void indexed() {
        isIndexed = true;
    }
}
