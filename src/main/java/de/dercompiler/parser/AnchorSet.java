package de.dercompiler.parser;

import de.dercompiler.lexer.token.*;

import java.util.EnumSet;

public class AnchorSet {

    private final EnumSet<Token> keywordsAndSeparators;
    private boolean operators;
    private boolean number;
    private boolean ident;
    private boolean type;

    public AnchorSet() {
        this(EnumSet.noneOf(Token.class), false, false, false, false);
    }

    private AnchorSet(EnumSet<Token> keywordsAndSeparators, boolean operators, boolean number, boolean ident, boolean type) {
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

    public AnchorSet fork(IToken... tokens) {
        EnumSet<Token> add = keywordsAndSeparators.clone();
        AnchorSet forked = new AnchorSet(add, operators, number, ident, type);
        for (IToken token : tokens) {
            if (token instanceof Token t) forked.keywordsAndSeparators.add(t);
            else if (token instanceof IdentifierToken t) forked.addIdent();
            else if (token instanceof IntegerToken t) forked.addInteger();
            else if (token instanceof OperatorToken t) forked.addOperator();
            else if (token instanceof TypeToken t) forked.addType();
        }
        return forked;
    }

    public AnchorSet addOperator() {
        operators = true;
        return this;
    }

    public AnchorSet addInteger() {
        number = true;
        return this;
    }

    public AnchorSet addIdent() {
        ident = true;
        return this;
    }

    public AnchorSet addType() {
        type = true;
        return this;
    }
}
