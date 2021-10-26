package de.dercompiler.parser;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.expression.ExpressionFactory;
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

    public AbstractExpression parseExpression() {
        return parseExpression(0);
    }

    private AbstractExpression parseExpression(int minPrec) {
        //result
        AbstractExpression result = parser.parseUnaryExp();

        IToken token = expectOperatorToken();
        int prec;
        while (!Objects.isNull(token) && (prec = precedenceOfOperation(token)) >= minPrec) {
            AbstractExpression rhs = parseExpression(prec + 1);
            result = ExpressionFactory.createExpression(token, result, rhs);
            token = expectOperatorToken();
        }
        return result;
    }

    private IToken expectOperatorToken() {
        //return null, when 
        return null;
    }
}
