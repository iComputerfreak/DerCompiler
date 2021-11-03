package de.dercompiler.lexer.token;

public enum OperatorToken implements IToken {
    ASSIGN("=", 0), EQUAL("==", 3),
    NOT_EQUAL("!=", 3), NOT("!", -1),
    PLUS("+", 5), ADD_SHORT("+=", 0), INCREMENT("++", -1),
    MINUS("-", 5), SUB_SHORT("-=", 0), DECREMENT("--", 5),
    STAR("*", 6), MULT_SHORT("*=", 0),
    SLASH("/", 6), DIV_SHORT("/=", 0),
    MODULO_SHORT("%=", -1), PERCENT_SIGN("%", -1),

    L_SHIFT_SHORT("<<=", -1), L_SHIFT("<<", -1),
    LESS_THAN_EQUAL("<=", 4), LESS_THAN("<", 4),
    GREATER_THAN_EQUAL(">=", 4), R_SHIFT_SHORT(">>=", -1),
    R_SHIFT_LOGICAL_SHORT(">>>=", -1), R_SHIFT_LOGICAL(">>>", -1),
    R_SHIFT(">>", -1), GREATER_THAN(">", 4),

    OR_SHORT("|=", 0), OR_LAZY("||", 1), BAR("|", -1),
    AND_SHORT("&=", 0), AND_LAZY("&&", 2), AMPERSAND("&", -1),
    NOT_LOGICAL("~", -1),

    XOR_SHORT("^=", -1), XOR("^", -1),
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
}
