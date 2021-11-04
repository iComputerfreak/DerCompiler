package de.dercompiler.ast.expression;

import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.OperatorToken;

public class ExpressionFactory {

    public static AbstractExpression createExpression(IToken token, AbstractExpression lhs, AbstractExpression rhs) {
        if (!(token instanceof OperatorToken t)) {
            return null;
        }
        return switch (t) {
            case ASSIGN -> new AssignmentExpression(lhs, rhs);
            case OR_LAZY -> new LogicalOrExpression(lhs, rhs);
            case AND_LAZY -> new LogicalAndExpression(lhs, rhs);
            case EQUAL -> new EqualExpression(lhs, rhs);
            case NOT_EQUAL -> new NotEqualExpression(lhs, rhs);
            case LESS_THAN -> new LessExpression(lhs, rhs);
            case LESS_THAN_EQUAL -> new LessEqualExpression(lhs, rhs);
            case GREATER_THAN -> new GreaterExpression(lhs, rhs);
            case GREATER_THAN_EQUAL -> new GreaterEqualExpression(lhs, rhs);
            case PLUS -> new AddExpression(lhs, rhs);
            // MINUS and NOT are handled in Parser.parseUnaryExpression
            case MINUS -> new SubtractExpression(lhs, rhs);
            case STAR -> new MultiplyExpression(lhs, rhs);
            case SLASH -> new DivisionExpression(lhs, rhs);
            case PERCENT_SIGN -> new ModuloExpression(lhs, rhs);
            default -> null;
        };

    }
}
