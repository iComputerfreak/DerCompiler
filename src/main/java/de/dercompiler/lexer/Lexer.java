package de.dercompiler.lexer;


import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.ErrorToken;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.IntegerToken;
import de.dercompiler.lexer.token.Token;
import de.dercompiler.util.RingBuffer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Lexer {

    private RingBuffer<IToken> tokenBuffer;
    private static final int SLL_CONSTANT = 4;
    private FileReader reader;

    private Position position;
    // FileReader.read() returns -1 for EOF, so char is not suitable
    private int currentChar;

    public Lexer(File input) {
        this.open(input);
        this.tokenBuffer = new RingBuffer<>(SLL_CONSTANT);
        this.position = new Position();
    }

    private void lex() {
        IToken token = null;
        boolean readNext = true;
        while (Character.isWhitespace(currentChar)) {
            readCharacter();
        }
        if (currentChar == -1) {
            token = Token.T_EOF;
        } else if (Character.isAlphabetic(currentChar) || currentChar == '_') {
            token = this.lexIdOrKeyword();
        } else if (Character.isDigit(currentChar)) {
            token = this.lexInteger();
        } else if (currentChar == '/') {
            readCharacter();
            readNext = false;
            if (currentChar == '*') {
                this.skipComment();
            } else {
                token = this.lexSymbolSequence('/');
            }
        } else {
            token = this.lexSymbolSequence(currentChar);
        }
        push(token);
        if (readNext) readCharacter();

    }

    private void skipComment() {
        //TODO
    }

    private IToken lexIdOrKeyword() {
        //TODO
        return null;
    }

    private IToken lexSymbolSequence(int prefix) {
        switch (prefix) {
            case '!':
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
            case '*':
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MULT_SHORT;
                } else {
                    return Token.STAR;
                }
            case '+':
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.ADD_SHORT;
                } else {
                    return Token.PLUS;
                }
            case ',':
                readCharacter();
                return Token.COMMA;
            case '-':
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.SUB_SHORT;
                } else {
                    return Token.MINUS;
                }
            case '.':
                readCharacter();
                return Token.DOT;
            case '/':
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.DIV_SHORT;
                } else {
                    return Token.SLASH;
                }
            case ':':
                readCharacter();
                return Token.COLON;
            case ';':
                readCharacter();
                return Token.SEMICOLON;
            case '<':
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
                readCharacter();
                return (currentChar == '=') ? Token.EQUAL : Token.ASSIGN;
            case '>':
                readCharacter();
                switch (currentChar) {
                    case '>':
                        readCharacter();
                        if (currentChar == '=') {
                            readCharacter();
                            return Token.R_SHIFT_SHORT;
                        } else {
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
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MODULO_SHORT;
                } else {
                    return Token.PERCENT_SIGN;
                }
            case '&':
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
                new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndContinue(LexerErrorIds.UNKNOWN_SYMBOL, "Unknown symbol: %c".formatted(currentChar));
                return new ErrorToken(LexerErrorIds.UNKNOWN_SYMBOL);
        }
    }

    private IntegerToken lexInteger() {
        StringBuilder intBuilder = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            intBuilder.append(currentChar);
            readCharacter();
        }
        String valueString = intBuilder.toString();
        if (valueString.startsWith("0") && valueString.length() > 1) {
            //TODO: Maybe return special "ErrorToken"
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndContinue(LexerErrorIds.INVALID_INTEGER_LITERAL, "Invalid integer literal: starts with 0 but is not 0");
        }
        int value = Integer.parseInt(valueString);
        return new IntegerToken(value);
    }

    private void readCharacter() {
        try {
            int oldChar = currentChar;
            currentChar = (char) reader.read();
            if (oldChar == '\n') {
                this.position.newLine();
            } else {
                this.position.advance();
            }
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(GeneralErrorIds.IO_EXCEPTION, "Error while reading input file.");
        }
    }

    public IToken nextToken() {
        IToken res = tokenBuffer.pop();
        this.lex();
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
