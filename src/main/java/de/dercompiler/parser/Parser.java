package de.dercompiler.parser;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.lexer.Lexer;

public class Parser {

    Lexer lexer;
    PrecedenceParser precedenceParser;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        precedenceParser = new PrecedenceParser(lexer, this);
    }

    public AbstractExpression parseUnaryExp() {
        return null;
    }

}
