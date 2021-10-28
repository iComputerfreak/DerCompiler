package de.dercompiler.lexer.token;

public sealed interface IToken permits ErrorToken, IdentifierToken, IntegerToken, Token {

    public String toString();

}
