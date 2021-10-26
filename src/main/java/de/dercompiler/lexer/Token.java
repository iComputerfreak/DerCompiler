package de.dercompiler.lexer;

public enum Token implements IToken {
    T_EOF("EOF"),

    ;

    private String id;

    Token(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}
