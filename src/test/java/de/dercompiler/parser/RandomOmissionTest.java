package de.dercompiler.parser;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.IdentifierToken;
import de.dercompiler.lexer.token.IntegerToken;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.dercompiler.lexer.token.Token.EOF;

public class RandomOmissionTest {

    private static final int OMISSION_COUNT = 2;

    @Test
    public void omissionTest() {
        File linesweep = ParserTestHelper.getLineSweep();
        Lexer lexer = Lexer.forFile(linesweep);

        List<TokenOccurrence> tokens = new ArrayList<>();
        TokenOccurrence t;
        do {
            t = lexer.nextToken();
            tokens.add(t);
        } while (t.type() != EOF);

        for (int i = 0; i < OMISSION_COUNT; i++) {
            int index = (int) (Math.random() * tokens.size());
            t = tokens.remove(index);
            System.out.println("Removed " + t.toString());
        }

        String input = tokensToString(tokens);
        Parser p = new Parser(Lexer.forString(input));
        p.parseProgram();

    }

    @Test
    public void insertionTest() {
        File linesweep = ParserTestHelper.getLineSweep();
        Lexer lexer = Lexer.forFile(linesweep);

        List<TokenOccurrence> tokens = new ArrayList<>();
        TokenOccurrence t;
        do {
            t = lexer.nextToken();
            tokens.add(t);
        } while (t.type() != EOF);

        for (int i = 0; i < OMISSION_COUNT; i++) {
            int index = (int) (Math.random() * tokens.size());
            int target = (int) (Math.random() * tokens.size());

            t = tokens.get(index);
            tokens.add(target, t);
            System.out.println("Inserted " + t.type() + " at " + tokens.get(target).position());
        }

        String input = tokensToString(tokens);
        Parser p = new Parser(Lexer.forString(input));
        p.parseProgram();

    }


    private String tokensToString(List<TokenOccurrence> tokens) {
        return tokens.stream()
                .map(TokenOccurrence::type)
                .map((token) -> {
                    if (token instanceof IdentifierToken id) {
                        return id.getIdentifier();
                    } else if (token instanceof IntegerToken integer) {
                        return integer.getValue();
                    } else if (token == EOF) {
                        return "";
                    }
                    return token.toString();
                }).collect(Collectors.joining(" "));
    }

}
