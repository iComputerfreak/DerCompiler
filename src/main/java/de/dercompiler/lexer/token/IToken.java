package de.dercompiler.lexer.token;

public sealed interface IToken permits ErrorToken, IdentifierToken, IntegerToken, OperatorToken, Token, TypeToken {

    String toString();

}
