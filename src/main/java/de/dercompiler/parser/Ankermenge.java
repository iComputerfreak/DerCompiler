package de.dercompiler.parser;

import de.dercompiler.lexer.token.*;

import java.util.EnumSet;

public class Ankermenge {

    private EnumSet<Token> keywordsAndSeparators;
    private boolean operators;
    private boolean number;
    private boolean ident;
    private boolean type;

    public Ankermenge() {
        this(EnumSet.noneOf(Token.class), false, false, false, false);
    }

    private Ankermenge(EnumSet<Token> keywordsAndSeparators, boolean operators, boolean number, boolean ident, boolean type) {
        keywordsAndSeparators.add(Token.EOF);
        this.keywordsAndSeparators = keywordsAndSeparators;
        this.operators = operators;
        this.number = number;
        this.ident = ident;
        this.type = type;
    }

    public boolean hasToken(IToken token) {
        if (token instanceof Token t) return keywordsAndSeparators.contains(t);
        if (token instanceof IdentifierToken t) return ident;
        if (token instanceof IntegerToken t) return number;
        if (token instanceof OperatorToken t) return operators;
        if (token instanceof TypeToken t) return type;
        return false;
    }

    public Ankermenge fork(Token... tokens) {
        EnumSet<Token> add = keywordsAndSeparators.clone();
        for (Token t : tokens) {
            add.add(t);
        }
        return new Ankermenge(add, operators, number, ident, type);
    }

    public Ankermenge addOperator() {
        operators = true;
        return this;
    }

    public Ankermenge addInteger() {
        number = true;
        return this;
    }

    public Ankermenge addIdent() {
        ident = true;
        return this;
    }

    public Ankermenge addType() {
        type = true;
        return this;
    }
}
