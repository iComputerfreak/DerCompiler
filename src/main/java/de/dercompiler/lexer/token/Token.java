package de.dercompiler.lexer.token;

public enum Token implements IToken {
    // keywords
    ABSTRACT("abstract"),
    ASSERT("assert"),
    BREAK("break"),
    CASE("case"),
    CATCH("catch"),
    CLASS("class"),
    CONST("const"),
    CONTINUE("continue"),
    DEFAULT("default"),
    DO("do"),
    ELSE("else"),
    ENUM("enum"),
    EXTENDS("extends"),
    FALSE("false"),
    FINALLY("finally"),
    FINAL("final"),
    FOR("for"),
    GOTO("goto"),
    IF("if"),
    IMPLEMENTS("implements"),
    IMPORT("import"),
    INSTANCE_OF("instanceof"),
    INTERFACE("interface"),
    NATIVE("native"),
    NEW("new"),
    NULL("null"),
    PACKAGE("package"),
    PRIVATE("private"),
    PROTECTED("protected"),
    PUBLIC("public"),
    RETURN("return"),
    STATIC("static"),
    STRICTFP("strictfp"),
    SUPER("super"),
    SWITCH("switch"),
    SYNCHRONIZED("synchronized"),
    THIS("this"),
    THROWS("throws"),
    THROW("throw"),
    TRANSIENT("transient"),
    TRUE("true"),
    TRY("try"),
    VOLATILE("volatile"),
    WHILE("while"),

    // separators

    DOT("."), COMMA(","),
    COLON(":"), SEMICOLON(";"),
    QUESTION_MARK("?"),

    L_PAREN("("), R_PAREN(")"),
    L_SQUARE_BRACKET("["), R_SQUARE_BRACKET("]"),
    L_CURLY_BRACKET("{"), R_CURLY_BRACKET("}"),

    EOF("EOF");

    private final String id;

    Token(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}
