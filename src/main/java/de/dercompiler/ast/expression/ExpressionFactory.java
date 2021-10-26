package de.dercompiler.ast.expression;

import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.Token;

import static de.dercompiler.lexer.token.Token.*;

public class ExpressionFactory {

    public static AbstractExpression createExpression(IToken token, AbstractExpression lhs, AbstractExpression rhs) {
        if (token instanceof Token t)
        switch (t) {

        }
        return null;
    }
}
