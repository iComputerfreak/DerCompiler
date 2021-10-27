package de.dercompiler.lexer;


import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.*;
import de.dercompiler.util.RingBuffer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Lexer {

    private static final int SLL_CONSTANT = 4;
    private final RingBuffer<IToken> tokenBuffer;
    private FileReader reader;

    private final Position position;
    // FileReader.read() returns -1 for EOF, so char is not suitable
    private int currentChar;

    public Lexer(File input) {
        this.open(input);
        this.tokenBuffer = new RingBuffer<>(SLL_CONSTANT);
        this.position = new Position();

        readCharacter();
    }

    private void lex() {
        IToken token = null;
        boolean readNext = true;
        while (token == null) {
            readNext = true;
            while (Character.isWhitespace(currentChar)) {
                readCharacter();
            }
            if (currentChar == -1) {
                token = Token.EOF;
            } else if (Character.isAlphabetic(currentChar) || currentChar == '_') {
                token = this.lexIdOrKeyword();
                readNext = false;
            } else if (Character.isDigit(currentChar)) {
                token = this.lexInteger();
                readNext = false;
            } else {
                readNext = false;
                token = this.lexSymbolSequence(currentChar);
            }
        }
        push(token);
        if (readNext) readCharacter();

    }

    private void skipComment() {
        while (true) {
            if (currentChar == -1) {
                new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.UNCLOSED_COMMENT, "Unclosed comment, expected '*/'");
            }
            if (currentChar != '*') {
                readCharacter();
                continue;
            }

            readCharacter();
            if (currentChar == '/') break;
        }
        readCharacter();
    }

    private IToken lexIdOrKeyword() {
        switch (currentChar) {
            case 'a':
                // abstract or assert
                readCharacter();
                switch (currentChar) {
                    case 'b':
                        readCharacter();
                        return compareSuffix(Token.ABSTRACT, 2);
                    case 's':
                        readCharacter();
                        return compareSuffix(Token.ASSERT, 2);
                    default:
                        return parseId("a");
                }
            case 'b':
                // boolean, break or byte
                readCharacter();
                switch (currentChar) {
                    case 'o':
                        readCharacter();
                        return compareSuffix(Token.BOOLEAN_TYPE, 2);
                    case 'r':
                        readCharacter();
                        return compareSuffix(Token.BREAK, 2);
                    case 'y':
                        readCharacter();
                        return compareSuffix(Token.BYTE_TYPE, 2);
                    default:
                        return parseId("b");
                }
            case 'c':
                // case, catch, char, class, const or continue
                readCharacter();
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        switch (currentChar) {
                            case 's':
                                readCharacter();
                                return compareSuffix(Token.CASE, 3);
                            case 't':
                                readCharacter();
                                return compareSuffix(Token.CATCH, 3);
                            default:
                                return parseId("ca");
                        }
                    case 'h':
                        readCharacter();
                        return compareSuffix(Token.CHARACTER_TYPE, 2);
                    case 'l':
                        readCharacter();
                        return compareSuffix(Token.CLASS, 2);
                    case 'o':
                        readCharacter();
                        if (currentChar == 'n') {
                            readCharacter();
                            switch (currentChar) {
                                case 's':
                                    readCharacter();
                                    return compareSuffix(Token.CONST, 4);
                                case 't':
                                    readCharacter();
                                    return compareSuffix(Token.CONTINUE, 4);
                                default:
                                    return parseId("con");
                            }
                        } else {
                            return parseId("co");
                        }
                }
            case 'd':
                readCharacter();
                switch (currentChar) {
                    case 'e':
                        readCharacter();
                        return compareSuffix(Token.DEFAULT, 2);
                    case 'o':
                        readCharacter();
                        if (!isIdentifierChar(currentChar)) {
                            readCharacter();
                            return Token.DO;
                        } else return compareSuffix(Token.DOUBLE_TYPE, 2);
                    default:
                        return parseId("d");
                }
            case 'e':
                readCharacter();
                switch (currentChar) {
                    case 'l':
                        readCharacter();
                        return compareSuffix(Token.ELSE, 2);
                    case 'n':
                        readCharacter();
                        return compareSuffix(Token.ENUM, 2);
                    case 'x':
                        readCharacter();
                        return compareSuffix(Token.EXTENDS, 2);
                    default:
                        return parseId("e");
                }
            case 'f':
                readCharacter();
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        return compareSuffix(Token.FALSE, 2);
                    case 'i':
                        readCharacter();
                        char[] chars = {'n', 'a', 'l'};
                        for (int i = 0; i < 3; i++) {
                            if (currentChar != chars[i]) {
                                return parseId("final".substring(0, 2 + i));
                            }
                            readCharacter();
                        }
                        //final or finally
                        if (!isIdentifierChar(currentChar)) return Token.FINAL;
                        else return compareSuffix(Token.FINALLY, 5);
                    case 'l':
                        readCharacter();
                        return compareSuffix(Token.FLOAT_TYPE, 2);
                    case 'o':
                        readCharacter();
                        return compareSuffix(Token.FOR, 2);
                    default:
                        return parseId("f");
                }

            case 'g':
                readCharacter();
                return compareSuffix(Token.GOTO, 1);
            case 'i':
                readCharacter();
                switch (currentChar) {
                    case 'f':
                        readCharacter();
                        if (!isIdentifierChar(currentChar)) return Token.IF;
                        else return parseId("if");
                    case 'm':
                        readCharacter();
                        if (currentChar != 'p') return parseId("im");
                        readCharacter();
                        switch (currentChar) {
                            case 'l':
                                readCharacter();
                                return compareSuffix(Token.IMPLEMENTS, 4);
                            case 'o':
                                readCharacter();
                                return compareSuffix(Token.IMPORT, 4);
                            default:
                                return parseId("imp");
                        }
                    case 'n':
                        readCharacter();
                        switch (currentChar) {
                            case 's':
                                readCharacter();
                                return compareSuffix(Token.INSTANCE_OF, 3);
                            case 't':
                                readCharacter();
                                if (!isIdentifierChar(currentChar)) {
                                    return Token.INT_TYPE;
                                } else {
                                    return compareSuffix(Token.INTERFACE, 3);
                                }
                            default:
                                return parseId("in");
                        }
                    default:
                        return parseId("i");
                }
            case 'l':
                readCharacter();
                return compareSuffix(Token.LONG_TYPE, 1);
            case 'n':
                readCharacter();
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        return compareSuffix(Token.NATIVE, 2);
                    case 'e':
                        readCharacter();
                        return compareSuffix(Token.NEW, 2);
                    case 'u':
                        readCharacter();
                        return compareSuffix(Token.NULL, 2);
                    default:
                        return parseId("n");
                }
            case 'p':
                readCharacter();
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        return compareSuffix(Token.PACKAGE, 2);
                    case 'r':
                        readCharacter();
                        switch (currentChar) {
                            case 'i':
                                readCharacter();
                                return compareSuffix(Token.PRIVATE, 3);
                            case 'o':
                                readCharacter();
                                return compareSuffix(Token.PROTECTED, 3);
                            default:
                                return parseId("pr");
                        }
                    case 'u':
                        readCharacter();
                        return compareSuffix(Token.PUBLIC, 2);
                    default:
                        parseId("p");
                }
            case 'r':
                readCharacter();
                return compareSuffix(Token.RETURN, 1);
            case 's':
                readCharacter();
                switch (currentChar) {
                    case 'h':
                        readCharacter();
                        return compareSuffix(Token.SHORT_TYPE, 2);
                    case 't':
                        readCharacter();
                        switch (currentChar) {
                            case 'a':
                                readCharacter();
                                return compareSuffix(Token.STATIC, 3);
                            case 'r':
                                readCharacter();
                                return compareSuffix(Token.STRICTFP, 3);
                            default:
                                return parseId("st");
                        }
                    case 'u':
                        readCharacter();
                        return compareSuffix(Token.SUPER, 2);
                    case 'w':
                        readCharacter();
                        return compareSuffix(Token.SWITCH, 2);
                    case 'y':
                        readCharacter();
                        return compareSuffix(Token.SYNCHRONIZED, 2);
                    default:
                        return parseId("s");
                }
            case 't':
                readCharacter();
                switch (currentChar) {
                    case 'h':
                        readCharacter();
                        switch (currentChar) {
                            case 'i':
                                readCharacter();
                                return compareSuffix(Token.THIS, 3);
                            case 'r':
                                readCharacter();
                                char[] chars = {'o', 'w'};
                                for (int i = 0; i < chars.length; i++) {
                                    if (currentChar != chars[i]) {
                                        return parseId("throw".substring(0, 3 + i));
                                    }
                                    readCharacter();
                                }
                                // throw or throws
                                if (!isIdentifierChar(currentChar)) return Token.THROW;
                                else return compareSuffix(Token.THROWS, 5);
                            default:
                                return parseId("th");
                        }
                    case 'r':
                        readCharacter();
                        switch (currentChar) {
                            case 'a':
                                readCharacter();
                                return compareSuffix(Token.TRANSIENT, 3);
                            case 'u':
                                readCharacter();
                                return compareSuffix(Token.TRUE, 3);
                            case 'y':
                                readCharacter();
                                return compareSuffix(Token.TRY, 3);
                            default:
                                return parseId("tr");
                        }
                    default:
                        return parseId("t");
                }
            case 'v':
                readCharacter();
                if (currentChar != 'o') return parseId("v");
                readCharacter();
                switch (currentChar) {
                    case 'i':
                        readCharacter();
                        return compareSuffix(Token.VOID, 3);
                    case 'l':
                        readCharacter();
                        return compareSuffix(Token.VOLATILE, 3);
                    default:
                        return parseId("vo");
                }
            case 'w':
                readCharacter();
                return compareSuffix(Token.WHILE, 1);
            default:
                return parseId("");
        }

    }

    private IToken compareSuffix(Token successToken, int pos) {
        String keyword = successToken.toString();
        for (int i = pos; i < keyword.length(); i++) {
            if (keyword.charAt(i) != currentChar) {
                return parseId(keyword.substring(0, i));
            }
            readCharacter();
        }
        // Now, keyword ought to be complete.
        if (isIdentifierChar(currentChar)) {
            return parseId(keyword);
        }
        return successToken;
    }

    private IToken parseId(String keyword) {
        StringBuilder sb = new StringBuilder(keyword);
        while (isIdentifierChar(currentChar)) {
            sb.append((char) currentChar);
            readCharacter();
        }
        return IdentifierToken.forIdentifier(sb.toString());
    }

    /**
     * Is true if the character is a valid symbol in an identifier (excluding the start).
     *
     * @param c the codepoint to be checked
     * @return true if c is a digit, a letter or an underscore
     */
    private boolean isIdentifierChar(int c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    private IToken lexSymbolSequence(int prefix) {
        switch (prefix) {
            case '!':
                // ! or !=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.NOT_EQUAL;
                } else {
                    return Token.NOT;
                }

            case '(':
                readCharacter();
                return Token.L_PAREN;
            case ')':
                readCharacter();
                return Token.R_PAREN;

            case '*':
                // * or *=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MULT_SHORT;
                } else {
                    return Token.STAR;
                }

            case '+':
                // +, ++ or +=
                readCharacter();
                switch (currentChar) {
                    case '=':
                        readCharacter();
                        return Token.ADD_SHORT;
                    case '+':
                        readCharacter();
                        return Token.INCREMENT;
                    default:
                        return Token.PLUS;
                }

            case ',':
                readCharacter();
                return Token.COMMA;

            case '-':
                // -, -= or --
                readCharacter();
                switch (currentChar) {
                    case '=':
                        readCharacter();
                        return Token.SUB_SHORT;
                    case '+':
                        readCharacter();
                        return Token.DECREMENT;
                    default:
                        return Token.MINUS;
                }

            case '.':
                readCharacter();
                return Token.DOT;

            case '/':
                // /, /= or /*
                readCharacter();
                switch (currentChar) {
                    case '=':
                        readCharacter();
                        return Token.DIV_SHORT;
                    case '*':
                        readCharacter();
                        this.skipComment();
                        return null;
                    default:
                        return Token.SLASH;
                }

            case ':':
                readCharacter();
                return Token.COLON;

            case ';':
                readCharacter();
                return Token.SEMICOLON;

            case '<':
                // <, <=, << or <<=
                readCharacter();
                switch (currentChar) {
                    case '<':
                        readCharacter();
                        if (currentChar == '=') {
                            readCharacter();
                            return Token.L_SHIFT_SHORT;
                        } else {
                            return Token.L_SHIFT;
                        }
                    case '=':
                        return Token.LESS_THAN_EQUAL;
                    default:
                        return Token.LESS_THAN;
                }

            case '=':
                // = or ==
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.EQUAL;
                } else {
                    return Token.ASSIGN;
                }

            case '>':
                // >, >=, >>, >>=, >>> or >>>==
                readCharacter();
                switch (currentChar) {
                    case '>':
                        readCharacter();
                        switch (currentChar) {
                            case '=':
                                readCharacter();
                                return Token.R_SHIFT_SHORT;
                            case '>':
                                readCharacter();
                                if (currentChar == '=') {
                                    readCharacter();
                                    return Token.R_SHIFT_LOGICAL_SHORT;
                                } else {
                                    return Token.R_SHIFT_LOGICAL;
                                }
                            default:
                                return Token.R_SHIFT;
                        }
                    case '=':
                        readCharacter();
                        return Token.GREATER_THAN_EQUAL;
                    default:
                        return Token.GREATER_THAN;
                }

            case '?':
                readCharacter();
                return Token.QUESTION_MARK;

            case '%':
                // % or %=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MODULO_SHORT;
                } else {
                    return Token.PERCENT_SIGN;
                }
            case '&':
                // &, && or &=
                readCharacter();
                switch (currentChar) {
                    case '&':
                        readCharacter();
                        return Token.AND_LAZY;
                    case '=':
                        readCharacter();
                        return Token.AND_SHORT;
                    default:
                        return Token.AMPERSAND;
                }
            case '[':
                readCharacter();
                return Token.L_SQUARE_BRACKET;
            case ']':
                readCharacter();
                return Token.R_SQUARE_BRACKET;

            case '^':
                // ^ or ^=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.XOR_SHORT;
                } else {
                    return Token.XOR;
                }

            case '{':
                readCharacter();
                return Token.L_CURLY_BRACKET;
            case '}':
                readCharacter();
                return Token.R_CURLY_BRACKET;
            case '~':
                readCharacter();
                return Token.NOT_LOGICAL;

            case '|':
                // |, || or |=
                readCharacter();
                switch (currentChar) {
                    case '|':
                        readCharacter();
                        return Token.OR_LAZY;
                    case '=':
                        readCharacter();
                        return Token.OR_SHORT;
                    default:
                        return Token.BAR;
                }
            default:
                new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.UNKNOWN_SYMBOL, "Unknown symbol: %c".formatted(currentChar));
                return new ErrorToken(LexerErrorIds.UNKNOWN_SYMBOL);
        }
    }

    private IToken lexInteger() {
        StringBuilder intBuilder = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            intBuilder.append((char) currentChar);
            readCharacter();
        }
        String valueString = intBuilder.toString();
        if (valueString.startsWith("0") && valueString.length() > 1) {
            //TODO: Maybe return special "ErrorToken"
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndContinue(LexerErrorIds.INVALID_INTEGER_LITERAL, "Invalid integer literal: starts with 0 but is not 0");
            return new ErrorToken(LexerErrorIds.INVALID_INTEGER_LITERAL);
        }
        int value = Integer.parseInt(valueString);
        return new IntegerToken(value);
    }

    private void readCharacter() {
        try {
            int oldChar = currentChar;
            currentChar = reader.read();
            if (oldChar == '\n') {
                this.position.newLine();
            } else {
                this.position.advance();
            }
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(GeneralErrorIds.IO_EXCEPTION, "Error while reading input file.", e);
        }
    }

    public IToken nextToken() {
        this.lex();
        IToken res = tokenBuffer.pop();
        return res;
    }

    public IToken peek(int lookAhead) {
        return tokenBuffer.peek(lookAhead);
    }

    private void push(IToken token) {
        tokenBuffer.push(token);
    }

    public Position getPosition() {
        return this.position;
    }

    private void open(File input) {
        try {
            this.reader = new FileReader(input);
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(GeneralErrorIds.FILE_NOT_FOUND, "Something went wrong while reading input file (" + input.getAbsolutePath() + ")!", e);
        }
    }

    private class Position {
        private int line;
        private int column;

        Position() {
            this.line = 0;
            this.column = 0;
        }

        public void newLine() {
            this.line++;
            this.column = 0;
        }

        public void advance() {
            this.column++;
        }
    }
}
