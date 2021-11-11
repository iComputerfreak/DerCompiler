package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.OperatorToken;

public class ExpressionFactory {

    public static Expression createExpression(IToken token, SourcePosition position, Expression lhs, Expression rhs) {
        if (!(token instanceof OperatorToken t)) {
            return null;
        }
        return switch (t) {
            case ASSIGN -> new AssignmentExpression(position, lhs, rhs);
            case OR_LAZY -> new LogicalOrExpression(position, lhs, rhs);
            case AND_LAZY -> new LogicalAndExpression(position, lhs, rhs);
            case EQUAL -> new EqualExpression(position, lhs, rhs);
            case NOT_EQUAL -> new NotEqualExpression(position, lhs, rhs);
            case LESS_THAN -> new LessExpression(position, lhs, rhs);
            case LESS_THAN_EQUAL -> new LessEqualExpression(position, lhs, rhs);
            case GREATER_THAN -> new GreaterExpression(position, lhs, rhs);
            case GREATER_THAN_EQUAL -> new GreaterEqualExpression(position, lhs, rhs);
            case PLUS -> new AddExpression(position, lhs, rhs);
            // unary MINUS and NOT are handled in Parser.parseUnaryExpression
            case MINUS -> new SubtractExpression(position, lhs, rhs);
            case STAR -> new MultiplyExpression(position, lhs, rhs);
            case SLASH -> new DivisionExpression(position, lhs, rhs);
            case PERCENT_SIGN -> new ModuloExpression(position, lhs, rhs);
            default -> new ErrorExpression(position);
        };

    }
}
