package de.dercompiler.parser;

import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.Lexer;

import java.util.Objects;

public class PrecedenceParser {

    Lexer lexer;
    Parser parser;

    public PrecedenceParser(Lexer lexer, Parser parser) {
        this.lexer = lexer;
    }

    private int precedenceOfOperation(IToken token) {

        return 0;
    }

    public void parseExpression() {
        parseExpression(0);
    }

    private void parseExpression(int minPrec) {
        //result
        parser.parseUnaryExp();

        IToken token = expectOperatorToken();
        int prec;
        //TODO really smaller? check after tokens get precedence
        while (!Objects.isNull(token) && (prec = precedenceOfOperation(token)) < minPrec) {
            //rhs
            parseExpression(prec + 1);
            //result = new Ast(operator, result, rhs);
            token = expectOperatorToken();
        }
        //result
        return;
    }

    private IToken expectOperatorToken() {
        //return null, when 
        return null;
    }
}
