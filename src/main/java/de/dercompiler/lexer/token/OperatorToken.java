package de.dercompiler.lexer.token;

public enum OperatorToken implements IToken {
    ASSIGN("=", 0), EQUAL("==", 6),
    NOT_EQUAL("!=", 6), NOT("!", 11),
    PLUS("+", 9), ADD_SHORT("+=", 0), INCREMENT("++", 11),
    MINUS("-", 9), SUB_SHORT("-=", 0), DECREMENT("--", 11),
    STAR("*", 10), MULT_SHORT("*=", 0),
    SLASH("/", 10), DIV_SHORT("/=", 0),
    MODULO_SHORT("%=", 0), PERCENT_SIGN("%", 10),

    L_SHIFT_SHORT("<<=", 0), L_SHIFT("<<", 8),
    LESS_THAN_EQUAL("<=", 7), LESS_THAN("<", 7),
    GREATER_THAN_EQUAL(">=", 7), R_SHIFT_SHORT(">>=", 0),
    R_SHIFT_LOGICAL_SHORT(">>>=", 0), R_SHIFT_LOGICAL(">>>", 8),
    R_SHIFT(">>", 8), GREATER_THAN(">", 7),

    OR_SHORT("|=", 0), OR_LAZY("||", 1), BAR("|", 3),
    AND_SHORT("&=", 0), AND_LAZY("&&", 2), AMPERSAND("&", 5),
    NOT_LOGICAL("~", 11),

    XOR_SHORT("^=", 0), XOR("^", 4),
    ;

    private final String id;
    private final int precedence;

    OperatorToken(String id, int precedence) {
        this.id = id;
        this.precedence = precedence;
    }

    public String getId() {
        return id;
    }

    public int getPrecedence() {
        return precedence;
    }

    @Override
    public String toString() {
        return getId();
    }
}
