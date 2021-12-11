package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.type.VoidType;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TargetTriple;

public final class MainMethod extends Method {

    private static boolean useRuntime = true;

    // INFO: methodRest may be null
    public MainMethod(SourcePosition position, SourcePosition voidPos, String identifier, Parameter arg, MethodRest methodRest, BasicBlock block) {
        super(position, new Type(voidPos, new VoidType(voidPos), 0), identifier, arg.asList(), methodRest, block);
    }
    
    /**
     * Returns the mangled identifier to use in firm
     */
    public String getMangledIdentifier() {

        return "main_func";
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof MainMethod otherMain) {
            // If this rest is null, but the other is not, return false
            return internalEquals(otherMain);
        }
        return false;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitMainMethod(this);
    }

    public static void useRuntime(boolean runtime) {
        useRuntime = runtime;
    }
}
