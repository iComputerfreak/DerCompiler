package de.dercompiler.parser;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.expression.ErrorExpression;
import de.dercompiler.ast.expression.ExpressionFactory;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
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

    private int operatorPrecedence(IToken token) {
        if (token instanceof OperatorToken ot) return ot.getPrecedence();
        return -1;
    }

    private AbstractExpression parseExpression(int minPrec) {

        AbstractExpression result = parser.parseUnaryExpression();
        IToken token = lexer.peek();
        SourcePosition pos = lexer.position();
        int prec;
        while (token instanceof OperatorToken op && (prec = op.getPrecedence()) >= minPrec) {
            if (prec == -1) {
                handleError(token);
            }
            token = lexer.nextToken();
            AbstractExpression rhs = parseExpression(prec + 1);
            result = ExpressionFactory.createExpression(op, pos, result, rhs);
            if (result instanceof ErrorExpression) {
                handleError(token);
            }
            token = lexer.peek();
            pos = lexer.position();
        }
        return result;
    }

    private void handleError(IToken token) {
        new OutputMessageHandler(MessageOrigin.PARSER)
                .printErrorAndExit(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN, "Token " + token + " is not supported. No Expression could be created!");
    }
}
