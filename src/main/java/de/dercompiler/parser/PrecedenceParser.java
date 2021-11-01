package de.dercompiler.parser;

import de.dercompiler.ast.expression.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.token.Token;

import java.util.Objects;

public class PrecedenceParser {

    private final LexerWrapper lexer;
    private final Parser parser;

    public PrecedenceParser(Lexer lexer, Parser parser) {
        this.lexer = new LexerWrapper(lexer);
        this.parser = parser;
    }

    private int precedenceOfOperation(IToken token) {
        if (token instanceof Token t) {
            return switch (t) {
                case ASSIGN -> 6;
                case OR_LAZY -> 5;
                case AND_LAZY -> 4;
                case EQUAL, NOT_EQUAL -> 3;
                case LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL -> 2;
                //in case your wonder, were NegativeExpression is created, look in Parser.parseUnaryExp()
                case PLUS, MINUS -> 1;
                case STAR, SLASH, PERCENT_SIGN -> 0;
                default -> -1;
            };
        }
        return -1;
    }

    public AbstractExpression parseExpression() {
        return parseExpression(0);
    }

    private AbstractExpression parseExpression(int minPrec) {

        AbstractExpression result = parser.parseUnaryExpression();
        int prec = precedenceOfOperation(lexer.peek());
        if (prec < 0) return result;
        IToken token = expectOperatorToken();
        while (!Objects.isNull(token) && (prec = precedenceOfOperation(token)) >= minPrec) {
            if (prec == -1) {
                handleError(token);
            }
            AbstractExpression rhs = parseExpression(prec + 1);
            result = ExpressionFactory.createExpression(token, result, rhs);
            if (Objects.isNull(result)) {
                handleError(token);
            }
            token = expectOperatorToken();
        }
        return result;
    }

    private IToken expectOperatorToken() {
        IToken token = lexer.peek();
        if (token instanceof Token t && Token.ASSIGN.ordinal() <= t.ordinal() && t.ordinal() <= Token.XOR.ordinal()) {
            lexer.nextToken();
            return token;
        }
        return null;
    }

    private void handleError(IToken token) {
        new OutputMessageHandler(MessageOrigin.PARSER, System.err)
                .printErrorAndExit(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN,"Token " + token + " is not supported. No Expression could be created!");
    }
}
