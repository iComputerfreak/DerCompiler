package de.dercompiler.lexer;


import com.sun.jna.platform.win32.WinDef;
import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.*;
import de.dercompiler.util.RingBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Stream;

/**
 * Represents a Lexer for MiniJava. It transforms a character input source into a buffered sequence of {@link IToken}s, which allows for a certain lookahead.
 */
public class Lexer {

    private static final int SLL_CONSTANT = 4;
    private final RingBuffer<TokenOccurrence> tokenBuffer;
    private BufferedReader reader;
    private Source source;

    // position of the currentChar in the source, NOT the position of the reader in the source (with is one char further along)
    private final Position position;
    // FileReader.read() returns -1 for EOF, so char is not suitable
    private int currentChar;

    /**
     * Creates a new {@link Lexer} for the input that the given reader produces.
     *
     * @param source Source for character input
     */
    public Lexer(Source source) {
        this.source = source;
        this.reader = source.getNewReader();
        this.tokenBuffer = new RingBuffer<>(SLL_CONSTANT);
        this.position = new Position();

        readCharacter();
    }

    /**
     * Lexes the input at the current position until an {@link IToken} is produced and saved into the buffer.
     */
    private void lex() {
        IToken token = null;
        SourcePosition currentPosition = null;

        while (token == null) {
            while (Character.isWhitespace(currentChar)) {
                readCharacter();
            }
            if (currentChar > 127) {
                fail(LexerErrorIds.UNKNOWN_SYMBOL, "Unknown symbol: %c".formatted(currentChar));
                token = new ErrorToken(LexerErrorIds.UNKNOWN_SYMBOL);
                readCharacter();
                continue;
            }
            currentPosition = getPosition();
            if (currentChar == -1) {
                token = Token.EOF;
            } else if (Character.isAlphabetic(currentChar) || currentChar == '_') {
                token = this.lexIdOrKeyword();
            } else if (Character.isDigit(currentChar)) {
                token = this.lexInteger();
            } else {
                token = this.lexSymbolSequence();
            }
        }
        push(new TokenOccurrence(token, currentPosition));

    }

    /**
     * Reads through the input until *&#47; is recognized.
     */
    private void skipComment() {
        while (true) {
            if (currentChar == -1) {
                fail(LexerErrorIds.UNCLOSED_COMMENT, "Unclosed comment, expected '*/'");
            }

            if (currentChar == '/') {
                readCharacter();
                // /.*
                if (currentChar == '*') {
                    // do not readCharacter here yet; might be beginning of */
                    // /*. | *./
                    new OutputMessageHandler(MessageOrigin.LEXER).printWarning(LexerWarningIds.NESTED_COMMENT, "Nested opened comments are not supported and ignored; there are no levels of 'comment depth'.");
                }
            }

            if (currentChar != '*') {
                readCharacter();
                continue;
            }
            readCharacter();
            // *./
            if (currentChar == '/') {
                // */.
                break;
            }
        }
        readCharacter();
    }

    /**
     * Lexes an identifier or alphabetic keyword (as opposed to a symbolic operator or separator).
     *
     * @return an IToken representing the current identifier or keyword
     */
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
                        return compareSuffix(TypeToken.BOOLEAN_TYPE, 2);
                    case 'r':
                        readCharacter();
                        // br.eak;
                        return compareSuffix(Token.BREAK, 2);
                    case 'y':
                        readCharacter();
                        // by.te
                        return compareSuffix(TypeToken.BYTE_TYPE, 2);
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
                        return compareSuffix(TypeToken.CHARACTER_TYPE, 2);
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
                    default:
                        return parseId("c");
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
                            return Token.DO;
                        } else return compareSuffix(TypeToken.DOUBLE_TYPE, 2);
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
                        return compareSuffix(TypeToken.FLOAT_TYPE, 2);
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
                                return compareSuffix(OperatorToken.INSTANCE_OF, 3);
                            case 't':
                                readCharacter();
                                // int. | int.erface
                                if (!isIdentifierChar(currentChar)) {
                                    return TypeToken.INT_TYPE;
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
                return compareSuffix(TypeToken.LONG_TYPE, 1);
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
                        return parseId("p");
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
                        return compareSuffix(TypeToken.SHORT_TYPE, 2);
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
                        return compareSuffix(TypeToken.VOID_TYPE, 3);
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

    /**
     * Checks whether the String keyword represented by the given Token follows. If so, returns that token. If not, parses an IdentifierToken.
     *
     * @param successToken a token that the following input is checked against
     * @param pos          position of currentChar inside the keyword, i.e. number of characters of the keyword that have already been read
     * @return successToken, if the keyword is read successfully, or else an IdentifierToken for the current identifier
     */
    private IToken compareSuffix(IToken successToken, int pos) {
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

    /**
     * Parses an identifier from the input, producing an IdentifierToken.
     *
     * @param prefix The beginning of the identifier
     * @return an {@link IdentifierToken} for the current identifier
     */
    private IdentifierToken parseId(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
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

    /**
     * Lexes an operator or separator, assuming currentChar is not a whitespace or alphanumerical character.
     *
     * @return a {@link Token} representing the following operator or separator, if successful.
     */
    private IToken lexSymbolSequence() {
        switch (currentChar) {
            case '!':
                readCharacter();
                // !. | !.=
                if (currentChar == '=') {
                    readCharacter();
                    return OperatorToken.NOT_EQUAL;
                } else {
                    return OperatorToken.NOT;
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
                    return OperatorToken.MULT_SHORT;
                } else {
                    return OperatorToken.STAR;
                }

            case '+':
                readCharacter();
                // +. | +.+ | +.=
                switch (currentChar) {
                    case '=':
                        readCharacter();
                        return OperatorToken.ADD_SHORT;
                    case '+':
                        readCharacter();
                        return OperatorToken.INCREMENT;
                    default:
                        return OperatorToken.PLUS;
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
                        return OperatorToken.SUB_SHORT;
                    case '-':
                        readCharacter();
                        return OperatorToken.DECREMENT;
                    default:
                        return OperatorToken.MINUS;
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
                        return OperatorToken.DIV_SHORT;
                    case '*':
                        readCharacter();
                        this.skipComment();
                        return null;
                    default:
                        return OperatorToken.SLASH;
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
                            return OperatorToken.L_SHIFT_SHORT;
                        } else {
                            return OperatorToken.L_SHIFT;
                        }
                    case '=':
                        readCharacter();
                        return OperatorToken.LESS_THAN_EQUAL;
                    default:
                        return OperatorToken.LESS_THAN;
                }

            case '=':
                // =. | =.=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return OperatorToken.EQUAL;
                } else {
                    return OperatorToken.ASSIGN;
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
                                return OperatorToken.R_SHIFT_SHORT;
                            case '>':
                                readCharacter();
                                // >>>. | >>>.=
                                if (currentChar == '=') {
                                    readCharacter();
                                    return OperatorToken.R_SHIFT_LOGICAL_SHORT;
                                } else {
                                    return OperatorToken.R_SHIFT_LOGICAL;
                                }
                            default:
                                return OperatorToken.R_SHIFT;
                        }
                    case '=':
                        readCharacter();
                        return OperatorToken.GREATER_THAN_EQUAL;
                    default:
                        return OperatorToken.GREATER_THAN;
                }

            case '?':
                readCharacter();
                return Token.QUESTION_MARK;

            case '%':
                // %. | %.=
                readCharacter();
                if (currentChar == '=') {
                    readCharacter();
                    return OperatorToken.MODULO_SHORT;
                } else {
                    return OperatorToken.PERCENT_SIGN;
                }
            case '&':
                // &. | &.& | &.=
                readCharacter();
                switch (currentChar) {
                    case '&':
                        readCharacter();
                        return OperatorToken.AND_LAZY;
                    case '=':
                        readCharacter();
                        return OperatorToken.AND_SHORT;
                    default:
                        return OperatorToken.AMPERSAND;
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
                    return OperatorToken.XOR_SHORT;
                } else {
                    return OperatorToken.XOR;
                }

            case '{':
                readCharacter();
                return Token.L_CURLY_BRACKET;
            case '}':
                readCharacter();
                return Token.R_CURLY_BRACKET;
            case '~':
                readCharacter();
                return OperatorToken.NOT_LOGICAL;

            case '|':
                // |. - |.| - |.=
                readCharacter();
                switch (currentChar) {
                    case '|':
                        readCharacter();
                        return OperatorToken.OR_LAZY;
                    case '=':
                        readCharacter();
                        return OperatorToken.OR_SHORT;
                    default:
                        return OperatorToken.BAR;
                }
            default:
                fail(LexerErrorIds.UNKNOWN_SYMBOL, "Unknown symbol: %c".formatted(currentChar));
                return new ErrorToken(LexerErrorIds.UNKNOWN_SYMBOL);
        }
    }

    /**
     * Lexes an integer, i.e. 0 or a sequence of digits that does not start with 0, assuming that currentChar is already a digit.
     * Any limitations on the length or the value of the integer are not put into place at this point.
     *
     * @return an {@link IntegerToken} representing the following integer.
     */
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

        return new IntegerToken(valueString);
    }

    /**
     * "Consumes" the currentChar and reads another one from the {@link Reader}.
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
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(GeneralErrorIds.IO_EXCEPTION, "Error while reading input file.", e);
        }
    }

    /**
     * Removes the next {@link TokenOccurrence} from the buffer and returns it.
     *
     * @return the next {@link TokenOccurrence}
     */
    public TokenOccurrence nextToken() {
        if (tokenBuffer.isEmpty()) {
            this.lex();
        }
        TokenOccurrence next = tokenBuffer.pop();
        this.lex();
        return next;
    }

    /**
     * Returns the n-th {@link TokenOccurrence} from the buffer without removing it from the buffer.
     *
     * @return the n-th {@link TokenOccurrence}
     */
    public TokenOccurrence peek(int lookAhead) {
        while (lookAhead < tokenBuffer.getCapacity() && lookAhead + 1 > tokenBuffer.getLength()) {
            lex();
        }
        return tokenBuffer.peek(lookAhead);
    }

    /**
     * Returns the next {@link TokenOccurrence} from the buffer without removing it from the buffer.
     *
     * @return the next {@link TokenOccurrence}
     */
    public TokenOccurrence peek() {
        if (tokenBuffer.isEmpty()) {
            lex();
        }
        return tokenBuffer.peek();
    }

    private void push(TokenOccurrence token) {
        tokenBuffer.push(token);
    }

    /**
     * @return the position of the next char of the input stream
     */
    private SourcePosition getPosition() {
        return this.position.copy();
    }

    void fail(LexerErrorIds id, String message) {
        OutputMessageHandler handler = new OutputMessageHandler(MessageOrigin.LEXER);
        handler.printErrorAndExit(id, message);
    }


    private static final int CONSOLE_WIDTH = 120;
    private static final int POINTER_OFFSET = 30;
    public String printSourceText(SourcePosition position) {
        // can only open Source once at a time, so reset reader and go to given line
        SourcePosition currentPosition = peek().position().copy();
        this.reader = this.source.getNewReader();
        this.tokenBuffer.clear();

        StringBuilder sb = new StringBuilder("In %s at line %s:\n".formatted(source.toString(), position.toString()));


        String line = this.reader.lines()
                .skip(position.getLine() - 1).findFirst().orElse("<empty line>");
        // Preserving tab width
        String indexLine = line.substring(0, position.getColumn() - 1).replaceAll("\\S", " ");

        // align long lines to the center of the terminal
        int trimFront = 0;
        if (position.getColumn() > CONSOLE_WIDTH - 3) {
            trimFront = position.getColumn() - POINTER_OFFSET;
            line = "..." + line.substring(position.getColumn() - (POINTER_OFFSET - 3));
        }
        if (line.length() > CONSOLE_WIDTH - 3) {
            line = line.substring(0, CONSOLE_WIDTH - 3) + "...";
        }

        sb.append(line);
        sb.append("\n");

        sb.append(indexLine.substring(trimFront));
        sb.append("^");

        this.reader = this.source.getNewReader();
        this.position.reset();
        // Reset reader to previous position, i.e. the position where peek() is the same token as before
        while (this.position.getLine() < currentPosition.getLine()) {
            this.nextLine();
        }
        while (this.position.getColumn() < currentPosition.getColumn()) {
            this.readCharacter();
        }
        lex();
        return sb.toString();
    }


    private String nextLine() {
        try {
            String line = reader.readLine();
            this.tokenBuffer.clear();
            if (line == null) {
                this.position.setColumn(line.length() + 1);
            } else {
                // position.newLine() is not suitable here, as the first character of the new line is not read yet
                // see readCharacter: here, when newLine() is called, currentChar is already the first char of the new line.
                this.position.line++;
                this.position.column = 0;
            }
            return line;
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(GeneralErrorIds.IO_EXCEPTION, "Error while reading input file.", e);
        }
        return null;
    }

    /**
     * Creates a {@link Lexer} that lexes the given {@link File}.
     *
     * @param file The {@link File} to lex
     * @return A {@link Lexer} for the file
     */
    public static Lexer forFile(File file) {
        return new Lexer(Source.forFile(file));
    }

    /**
     * Creates a {@link Lexer} that lexes the given {@link String}.
     *
     * @param input The {@link String} to lex
     * @return A {@link Lexer} for the input
     */
    public static Lexer forString(String input) {
        return new Lexer(Source.forString(input));
    }

    /**
     * Represents the position of a {@link Reader} in a source, counted as lines and columns. The initial Position is 1:1.
     */
    static class Position extends SourcePosition {

        Position() {
            super();
        }

        private void reset() {
            this.line = 1;
            this.column = 0;
        }

        public void newLine() {
            this.line++;
            this.column = 1;
        }

        public void advance() {
            this.column++;
        }

        public void setColumn(int column) {
            this.column = column;
        }
    }

}
