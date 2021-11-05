package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class ClassMember extends ASTNode permits Field, Method, MainMethod, ErrorClassMember {

    public ClassMember(SourcePosition position) {
        super(position);
    }

    public static class Comparator implements java.util.Comparator<ClassMember> {

            @Override
            public  int compare(ClassMember a, ClassMember b) {
                if (a instanceof Method ma) {
                    if (b instanceof Field) return -1;
                    else if (b instanceof Method mb)
                        return ma.getIdentifier().compareTo(mb.getIdentifier());
                    else if (b instanceof MainMethod mmb) {
                        return ma.getIdentifier().compareTo(mmb.getIdentifier());
                    }
                } else if (a instanceof MainMethod mma) {
                    if (b instanceof Field) return -1;
                    else if (b instanceof Method mb)
                        return mma.getIdentifier().compareTo(mb.getIdentifier());
                } else if (a instanceof Field fa) {
                    if (b instanceof Method || b instanceof MainMethod) return 1;
                    else if (b instanceof Field fb) {
                        return fa.getIdentifier().compareTo(fb.getIdentifier());
                    }
                }
                return 1;
            }

    }
}
