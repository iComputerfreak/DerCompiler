package de.dercompiler.ast;

import de.dercompiler.util.Utils;

import java.util.List;

public final class Program implements ASTNode {
    
    private final List<ClassDeclaration> classes;

    public Program(List<ClassDeclaration> classes) {
        this.classes = classes;
    }

    public List<ClassDeclaration> getClasses() {
        return classes;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof Program otherProgram) {
            return Utils.syntaxEquals(this.classes, otherProgram.classes);
        }
        return false;
    }
}
