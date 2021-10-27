package de.dercompiler.lexer.token;

public enum Token implements IToken {
    // keywords
    ABSTRACT("abstract"),
    ASSERT("assert"),
    BOOLEAN_TYPE("boolean"),
    BREAK("break"),
    BYTE_TYPE("byte"),
    CASE("case"),
    CATCH("catch"),
    CHARACTER_TYPE("char"),
    CLASS("class"),
    CONST("const"),
    CONTINUE("continue"),
    DEFAULT("default"),
    DO("do"),
    DOUBLE_TYPE("double"),
    ELSE("else"),
    ENUM("enum"),
    EXTENDS("extends"),
    FALSE("false"),
    FINALLY("finally"),
    FINAL("final"),
    FLOAT_TYPE("float"),
    FOR("for"),
    GOTO("goto"),
    IF("if"),
    IMPLEMENTS("implements"),
    IMPORT("import"),
    INSTANCE_OF("instanceof"),
    INTERFACE("interface"),
    INT_TYPE("int"),
    LONG_TYPE("long"),
    NATIVE("native"),
    NEW("new"),
    NULL("null"),
    PACKAGE("package"),
    PRIVATE("private"),
    PROTECTED("protected"),
    PUBLIC("public"),
    RETURN("return"),
    SHORT_TYPE("short"),
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
    VOID("void"),
    VOLATILE("volatile"),
    WHILE("while"),

    // operators and separators
    NOT_EQUAL("!="), NOT("!"),
    PLUS("+"), ADD_SHORT("+="), INCREMENT("++"),
    MINUS("-"), SUB_SHORT("-="), DECREMENT("--"),
    STAR("*"), MULT_SHORT("*="),
    SLASH("/"), DIV_SHORT("/="),
    MODULO_SHORT("%="), PERCENT_SIGN("%"),

    DOT("."), COMMA(","),
    COLON(":"), SEMICOLON(";"),
    QUESTION_MARK("?"),

    L_SHIFT_SHORT("<<="), L_SHIFT("<<"),
    LESS_THAN_EQUAL("<="), LESS_THAN("<"),
    EQUAL("=="), ASSIGN("="),
    GREATER_THAN_EQUAL(">="), R_SHIFT_SHORT(">>="),
    R_SHIFT_LOGICAL_SHORT(">>>="), R_SHIFT_LOGICAL(">>>"),
    R_SHIFT(">>"), GREATER_THAN(">"),

    OR_SHORT("|="), OR_LAZY("||"), BAR("|"),
    AND_SHORT("&="), AND_LAZY("&&"), AMPERSAND("&"),
    XOR_SHORT("^="), XOR("^"),

    L_PAREN("("), R_PAREN(")"),
    L_SQUARE_BRACKET("["), R_SQUARE_BRACKET("]"),
    L_CURLY_BRACKET("{"), R_CURLY_BRACKET("}"), NOT_LOGICAL("~"),

    EOF("EOF");

    private String id;

    Token(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}
