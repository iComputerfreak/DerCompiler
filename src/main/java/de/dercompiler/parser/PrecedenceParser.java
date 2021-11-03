package de.dercompiler.parser;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.expression.ExpressionFactory;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

public class PrecedenceParser {

    private final LexerWrapper lexer;
    private final Parser parser;

    public PrecedenceParser(Lexer lexer, Parser parser) {
        this.lexer = new LexerWrapper(lexer);
        this.parser = parser;
    }

    public AbstractExpression parseExpression() {
        return parseExpression(0);
    }

    private AbstractExpression parseExpression(int minPrec) {

        AbstractExpression result = parser.parseUnaryExpression();
        OperatorToken token = expectOperatorToken();
        if (Objects.isNull(token)) return result;

        int prec;
        while (!Objects.isNull(token) && (prec = token.getPrecedence()) >= minPrec) {
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

    private OperatorToken expectOperatorToken() {
        IToken token = lexer.peek();
        if (token instanceof OperatorToken op) {
            lexer.nextToken();
            return op;
        }
        return null;
    }

    private void handleError(IToken token) {
        new OutputMessageHandler(MessageOrigin.PARSER, System.err)
                .printErrorAndExit(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN, "Token " + token + " is not supported. No Expression could be created!");
    }
}
