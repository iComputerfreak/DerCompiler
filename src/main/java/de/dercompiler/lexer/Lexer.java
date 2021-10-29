package de.dercompiler.lexer;


import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.*;
import de.dercompiler.util.RingBuffer;

import java.io.*;


public class Lexer {

    private static final int SLL_CONSTANT = 4;
    private final RingBuffer<IToken> tokenBuffer;
    private Reader reader;

    private final Position position;
    // FileReader.read() returns -1 for EOF, so char is not suitable
    private int currentChar;

    public Lexer(Reader reader) {
        this.reader = reader;
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
                readCharacter();
                // a.bstract | a.ssert
                switch (currentChar) {
                    case 'b':
                        readCharacter();
                        // ab.stract
                        return compareSuffix(Token.ABSTRACT, 2);
                    case 's':
                        readCharacter();
                        // as.sert
                        return compareSuffix(Token.ASSERT, 2);
                    default:
                        return parseId("a");
                }
            case 'b':
                readCharacter();
                // b.oolean | b.reak | b.yte
                switch (currentChar) {
                    case 'o':
                        readCharacter();
                        // bo.olean
                        return compareSuffix(Token.BOOLEAN_TYPE, 2);
                    case 'r':
                        readCharacter();
                        // br.eak;
                        return compareSuffix(Token.BREAK, 2);
                    case 'y':
                        readCharacter();
                        // by.te
                        return compareSuffix(Token.BYTE_TYPE, 2);
                    default:
                        return parseId("b");
                }
            case 'c':
                // c.ase | c.atch | c.har | c.lass | c.onst | c.ontinue
                readCharacter();
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        // ca.se | ca.tch
                        switch (currentChar) {
                            case 's':
                                readCharacter();
                                // cas.e
                                return compareSuffix(Token.CASE, 3);
                            case 't':
                                readCharacter();
                                // cat.ch
                                return compareSuffix(Token.CATCH, 3);
                            default:
                                return parseId("ca");
                        }
                    case 'h':
                        readCharacter();
                        // ch.ar
                        return compareSuffix(Token.CHARACTER_TYPE, 2);
                    case 'l':
                        readCharacter();
                        // cl.ass
                        return compareSuffix(Token.CLASS, 2);
                    case 'o':
                        readCharacter();
                        // co.nst | co.ntinue
                        if (currentChar == 'n') {
                            readCharacter();
                            // con.st | con.tinue
                            switch (currentChar) {
                                case 's':
                                    readCharacter();
                                    // cons.t
                                    return compareSuffix(Token.CONST, 4);
                                case 't':
                                    readCharacter();
                                    // cont.inue
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
                // d.efault | d.o | d.ouble
                switch (currentChar) {
                    case 'e':
                        readCharacter();
                        // de.fault
                        return compareSuffix(Token.DEFAULT, 2);
                    case 'o':
                        readCharacter();
                        // do. | do.uble
                        if (!isIdentifierChar(currentChar)) {
                            readCharacter();
                            return Token.DO;
                        } else return compareSuffix(Token.DOUBLE_TYPE, 2);
                    default:
                        return parseId("d");
                }
            case 'e':
                readCharacter();
                // e.lse | e.num | e.xtends
                switch (currentChar) {
                    case 'l':
                        readCharacter();
                        // el.se
                        return compareSuffix(Token.ELSE, 2);
                    case 'n':
                        readCharacter();
                        // en.um
                        return compareSuffix(Token.ENUM, 2);
                    case 'x':
                        readCharacter();
                        // ex.tends
                        return compareSuffix(Token.EXTENDS, 2);
                    default:
                        return parseId("e");
                }
            case 'f':
                readCharacter();
                // f.alse | f.inal | f.inally | f.loat | f.or
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        // fa.lse
                        return compareSuffix(Token.FALSE, 2);
                    case 'i':
                        readCharacter();
                        // fi.nal | fi.nally
                        char[] chars = {'n', 'a', 'l'};
                        for (int i = 0; i < 3; i++) {
                            if (currentChar != chars[i]) {
                                return parseId("final".substring(0, 2 + i));
                            }
                            readCharacter();
                        }
                        // final. | final.ly
                        if (!isIdentifierChar(currentChar)) return Token.FINAL;
                        else return compareSuffix(Token.FINALLY, 5);
                    case 'l':
                        readCharacter();
                        // fl.oat
                        return compareSuffix(Token.FLOAT_TYPE, 2);
                    case 'o':
                        readCharacter();
                        // fo.r
                        return compareSuffix(Token.FOR, 2);
                    default:
                        return parseId("f");
                }

            case 'g':
                readCharacter();
                // g.oto
                return compareSuffix(Token.GOTO, 1);
            case 'i':
                readCharacter();
                // i.f | i.mplements | i.mport
                switch (currentChar) {
                    case 'f':
                        readCharacter();
                        // if.
                        if (!isIdentifierChar(currentChar)) return Token.IF;
                        else return parseId("if");
                    case 'm':
                        readCharacter();
                        // im.plements | im.port
                        if (currentChar != 'p') return parseId("im");
                        readCharacter();
                        //imp.lements | imp.ort
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
                        // in.stanceof | in.t | in.terface
                        switch (currentChar) {
                            case 's':
                                readCharacter();
                                // ins.tanceof
                                return compareSuffix(Token.INSTANCE_OF, 3);
                            case 't':
                                readCharacter();
                                // int. | int.erface
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
                // l.ong
                return compareSuffix(Token.LONG_TYPE, 1);
            case 'n':
                readCharacter();
                // n.ative | n.ew | n.ull
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        // na.tive
                        return compareSuffix(Token.NATIVE, 2);
                    case 'e':
                        readCharacter();
                        // ne.w
                        return compareSuffix(Token.NEW, 2);
                    case 'u':
                        readCharacter();
                        // nu.ll
                        return compareSuffix(Token.NULL, 2);
                    default:
                        return parseId("n");
                }
            case 'p':
                readCharacter();
                // p.ackage | p.rivate | p.rotected | p.ublic
                switch (currentChar) {
                    case 'a':
                        readCharacter();
                        // p.ackage
                        return compareSuffix(Token.PACKAGE, 2);
                    case 'r':
                        readCharacter();
                        // pr.ivate | pr.otected
                        switch (currentChar) {
                            case 'i':
                                readCharacter();
                                // pri.vate
                                return compareSuffix(Token.PRIVATE, 3);
                            case 'o':
                                readCharacter();
                                //pro.tected
                                return compareSuffix(Token.PROTECTED, 3);
                            default:
                                return parseId("pr");
                        }
                    case 'u':
                        readCharacter();
                        // pu.blic
                        return compareSuffix(Token.PUBLIC, 2);
                    default:
                        parseId("p");
                }
            case 'r':
                readCharacter();
                // r.eturn
                return compareSuffix(Token.RETURN, 1);
            case 's':
                readCharacter();
                // s.hort | s.tatic | s.trictfp | s.uper | s.witch | s.ynchronized
                switch (currentChar) {
                    case 'h':
                        readCharacter();
                        // sh.ort
                        return compareSuffix(Token.SHORT_TYPE, 2);
                    case 't':
                        readCharacter();
                        // st.atic | st.rictfp
                        switch (currentChar) {
                            case 'a':
                                readCharacter();
                                // sta.tic
                                return compareSuffix(Token.STATIC, 3);
                            case 'r':
                                readCharacter();
                                // str.ictfp
                                return compareSuffix(Token.STRICTFP, 3);
                            default:
                                return parseId("st");
                        }
                    case 'u':
                        readCharacter();
                        // su.per
                        return compareSuffix(Token.SUPER, 2);
                    case 'w':
                        readCharacter();
                        // sw.itch
                        return compareSuffix(Token.SWITCH, 2);
                    case 'y':
                        readCharacter();
                        // sy.nchronized
                        return compareSuffix(Token.SYNCHRONIZED, 2);
                    default:
                        return parseId("s");
                }
            case 't':
                readCharacter();
                // t.his | t.hrow | t.hrows | t.ransient | t.rue | t.ry
                switch (currentChar) {
                    case 'h':
                        readCharacter();
                        // th.is | th.row | th.rows
                        switch (currentChar) {
                            case 'i':
                                readCharacter();
                                // thi.s
                                return compareSuffix(Token.THIS, 3);
                            case 'r':
                                readCharacter();
                                // thr.ow | thr.ows
                                char[] chars = {'o', 'w'};
                                for (int i = 0; i < chars.length; i++) {
                                    if (currentChar != chars[i]) {
                                        return parseId("throw".substring(0, 3 + i));
                                    }
                                    readCharacter();
                                }
                                // throw. | throw.s
                                if (!isIdentifierChar(currentChar)) return Token.THROW;
                                else return compareSuffix(Token.THROWS, 5);
                            default:
                                return parseId("th");
                        }
                    case 'r':
                        readCharacter();
                        // tr.ansient | tr.ue | tr.y
                        switch (currentChar) {
                            case 'a':
                                readCharacter();
                                // tra.nsient
                                return compareSuffix(Token.TRANSIENT, 3);
                            case 'u':
                                readCharacter();
                                // tru.e
                                return compareSuffix(Token.TRUE, 3);
                            case 'y':
                                readCharacter();
                                // try.
                                return compareSuffix(Token.TRY, 3);
                            default:
                                return parseId("tr");
                        }
                    default:
                        return parseId("t");
                }
            case 'v':
                readCharacter();
                // v.oid | v.olatile
                if (currentChar != 'o') return parseId("v");
                readCharacter();
                // vo.id | vo.latile
                switch (currentChar) {
                    case 'i':
                        readCharacter();
                        // voi.d
                        return compareSuffix(Token.VOID, 3);
                    case 'l':
                        readCharacter();
                        // vol.atile
                        return compareSuffix(Token.VOLATILE, 3);
                    default:
                        return parseId("vo");
                }
            case 'w':
                readCharacter();
                // w.hile
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
                readCharacter();
                // !. | !.=
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
                readCharacter();
                // *. | *.=
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MULT_SHORT;
                } else {
                    return Token.STAR;
                }

            case '+':
                readCharacter();
                // +. | +.+ | +.=
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
                // -. | -.= | -.-
                readCharacter();
                switch (currentChar) {
                    case '=':
                        readCharacter();
                        return Token.SUB_SHORT;
                    case '-':
                        readCharacter();
                        return Token.DECREMENT;
                    default:
                        return Token.MINUS;
                }

            case '.':
                readCharacter();
                return Token.DOT;

            case '/':
                // /. | /.= | /.*
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
                // <. | <.= | <.< | <.<=
                readCharacter();
                switch (currentChar) {
                    case '<':
                        readCharacter();
                        // <<. | <<.=
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
                // =. | =.=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.EQUAL;
                } else {
                    return Token.ASSIGN;
                }

            case '>':
                readCharacter();
                // >. | >.= | >.> | >.>= | >.>> | >.>>==
                switch (currentChar) {
                    case '>':
                        readCharacter();
                        // >>. | >>.= | >>.> | >>.>=
                        switch (currentChar) {
                            case '=':
                                readCharacter();
                                return Token.R_SHIFT_SHORT;
                            case '>':
                                readCharacter();
                                // >>>. | >>>.=
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
                // %. | %.=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return Token.MODULO_SHORT;
                } else {
                    return Token.PERCENT_SIGN;
                }
            case '&':
                // &. | &.& | &.=
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
                // ^ | ^.=
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
                // |. - |.| - |.=
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
        if (currentChar == '0') {
            readCharacter();
            return new IntegerToken("0");
        }

        while (Character.isDigit(currentChar)) {
            intBuilder.append((char) currentChar);
            readCharacter();
        }
        String valueString = intBuilder.toString();

        if (!isInIntegerRange(valueString)) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndContinue(LexerErrorIds.INVALID_INTEGER_LITERAL, "Invalid integer literal: outside of valid integer range");
            return new ErrorToken(LexerErrorIds.INVALID_INTEGER_LITERAL);
        }
        return new IntegerToken(valueString);
    }

    private static boolean isInIntegerRange(String valueString) {
        int MAX_INT = Integer.MAX_VALUE;
        int MIN_INT = Integer.MIN_VALUE;
        int MAX_LENGTH = String.valueOf(MAX_INT).length();
        int length = valueString.length();

        switch (Integer.signum(Integer.compare(length, MAX_LENGTH))) {
            case -1: return true;
            case 0:
                long longValue = Long.valueOf(valueString);
                return MIN_INT <= longValue && longValue <= MAX_INT;
            case 1:
            default: return false;
        }
    }

    /**
     *  "Consumes" the currentChar and reads another one from the Reader.
     */
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

    public IToken peek() {
        return tokenBuffer.peek(0);
    }

    private void push(IToken token) {
        tokenBuffer.push(token);
    }

    public Position getPosition() {
        return this.position;
    }

    public static Lexer forFile(File file) {
        try {
            return new Lexer(new FileReader(file));
        } catch (FileNotFoundException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printErrorAndExit(GeneralErrorIds.FILE_NOT_FOUND, "Could not lex file: file not found or not readable.");
        }
        return null;
    }

    public static Lexer forString(String input) {
        return new Lexer(new StringReader(input));
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
