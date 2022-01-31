package de.dercompiler.parser;

import de.dercompiler.Program;
import de.dercompiler.ast.printer.PrettyPrinter;
import de.dercompiler.io.DebugEvent;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StepByStepParserTest {

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
        OutputMessageHandler.setTestOutput(false);
    }

    @AfterAll
    static void cleanup() {
        OutputMessageHandler.setTestOutput(true);
    }

    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    @Test
    @DisplayName("check file step by step")
    void stepByStep() {
        Lexer lexer = Lexer.forFile(ParserTestHelper.getLineSweep());
        TokenOccurrence token = lexer.nextToken();
        StringBuilder sb = new StringBuilder();
        do {
            OutputMessageHandler.clearDebugEvents();
            append(sb, token);
            Lexer curLexer = Lexer.forString(sb.toString());
            Parser parser = new Parser(curLexer);
            parser.parseProgram();
            List<DebugEvent> eventList = OutputMessageHandler.getEvents();
            if (eventList.isEmpty()) {
                System.out.println(curLexer.printSourceText(curLexer.peek().position()));
            }
            // We just parsed a single token as a program, the debug events should contain an error
            assertFalse(eventList.isEmpty());
            final String tokenPos = curLexer.peek().position().toString();
            boolean valid = eventList.stream().anyMatch((debugEvent -> debugEvent.getMessage().contains(tokenPos)));
            if (!valid) {
                System.out.println("Expected pos: " + tokenPos);
                eventList.forEach(debugEvent -> System.out.println(debugEvent.getMessage()));
            }
            assertTrue(valid);

            while((token = lexer.nextToken()).type() == Token.R_CURLY_BRACKET && lexer.peek().type() == Token.CLASS) append(sb, token);
        } while(lexer.peek(1).type() != Token.EOF);

        OutputMessageHandler.clearDebugEvents();
        Parser parser = new Parser(Lexer.forFile(ParserTestHelper.getLineSweep()));
        parser.parseProgram();
        boolean valid = OutputMessageHandler.getEvents().isEmpty();
        if (!valid) {
            OutputMessageHandler.getEvents().forEach(debugEvent -> System.out.println(debugEvent.getMessage()));
        }
        assertTrue(valid);
    }

    private void append(StringBuilder sb, TokenOccurrence token) {
        if (token.type() instanceof IdentifierToken it) {
            sb.append(it.getIdentifier());
        } else if (token.type() instanceof IntegerToken it) {
            sb.append(it.getValue());
        } else if (token.type() instanceof OperatorToken ot) {
            sb.append(ot.getId());
        } else {
            sb.append(token.type());
        }
        sb.append(" ");
    }

    @Test
    void parsePrintParsePrintParse() {
        PrettyPrinter printer = new PrettyPrinter(false);
        Parser parser = new Parser(Lexer.forFile(ParserTestHelper.getLineSweep()));
        Program first = parser.parseProgram();
        printer.visitProgram(first);
        String secondString = printer.flush();
        parser = new Parser(Lexer.forString(secondString));
        Program second = parser.parseProgram();
        printer.visitProgram(second);
        String thirdString = printer.flush();
        parser = new Parser(Lexer.forString(thirdString));
        Program third = parser.parseProgram();

        assertTrue(OutputMessageHandler.getEvents().isEmpty());
        boolean valid = second.syntaxEquals(third);
        if (!valid) {
            int i = 0;
            Scanner fs = new Scanner(secondString);
            Scanner ss = new Scanner(thirdString);
            while(fs.hasNextLine() && ss.hasNextLine()) {
                System.out.println("F " + i + ": " + fs.nextLine());
                System.out.println("S " + i + ": " + ss.nextLine());
                System.out.println("\n");
                i++;
            }
            while(fs.hasNextLine()) {
                System.out.println("F " + i + ": " + fs.nextLine());
                i++;
            }
            while(ss.hasNextLine()) {
                System.out.println("S " + i + ": " + ss.nextLine());
                i++;
            }
        }
        assertTrue(valid);
        assertTrue(secondString.equals(thirdString));
    }
}
