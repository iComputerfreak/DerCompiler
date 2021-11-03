package de.dercompiler.lexer.token;

public enum TypeToken implements IToken {
    BOOLEAN_TYPE("boolean"),
    BYTE_TYPE("byte"),
    CHARACTER_TYPE("char"),
    DOUBLE_TYPE("double"),
    FLOAT_TYPE("float"),
    INT_TYPE("int"),
    LONG_TYPE("long"),
    SHORT_TYPE("short"),
    VOID_TYPE("void"),
    ;

    private final String id;

    TypeToken(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
