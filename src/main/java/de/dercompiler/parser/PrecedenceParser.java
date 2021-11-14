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

public class PrecedenceParser {

    private final LexerWrapper lexer;
    private final Parser parser;

    public PrecedenceParser(Lexer lexer, Parser parser) {
        this.lexer = new LexerWrapper(lexer);
        this.parser = parser;
    }

    public AbstractExpression parseExpression(AnchorSet ank) {
        return parseExpression(ank, 0);
    }

    private AbstractExpression parseExpression(AnchorSet ank, int minPrec) {

        AbstractExpression result = parser.parseUnaryExpression(ank.fork().addOperator());
        IToken token = lexer.peek();
        SourcePosition pos = lexer.position();
        int prec;
        while (token instanceof OperatorToken op && (prec = op.getPrecedence()) >= minPrec) {
            if (prec == -1) {
                handleError(ank, token, pos, minPrec);
            }
            //don't assign token here, we need it maybe for error printing
            lexer.nextToken();
            //ank right or do we have to add
            AbstractExpression rhs = parseExpression(ank,prec + 1);
            result = ExpressionFactory.createExpression(op, pos, result, rhs);
            if (result instanceof ErrorExpression) {
                handleError(ank, token, pos, minPrec);
            }
            token = lexer.peek();
            pos = lexer.position();
        }
        return result;
    }

    private void handleError(AnchorSet ank, IToken token, SourcePosition position, int minPrec) {
        new OutputMessageHandler(MessageOrigin.PARSER)
                .printParserError(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN, "Token " + token + " is not supported", lexer.getLexer(), position);
        token = lexer.peek();
        //skip until operator is less then current, so we skip the current false tree and start parsing the next branch correct again
        while (!ank.hasToken(token)|| (token instanceof OperatorToken ot && ot.getPrecedence() < minPrec)) {
            token = lexer.nextToken();
        }
    }
}
