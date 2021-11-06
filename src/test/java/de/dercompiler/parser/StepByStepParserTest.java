package de.dercompiler.parser;

import de.dercompiler.ast.Program;
import de.dercompiler.io.DebugEvent;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.*;
import org.junit.jupiter.api.*;

import java.util.List;

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
        String sweepline =
                """
class Point {
    public int x;
    public int y;
}
                
class Line {
    public Point start;
    public Point end;
                
    public int id;
}
                
class LineSorter {
                
    public void sortPointsInLines(Line[] lines, int length) {
        int i = 0;
        while(i < length) {
            if (lines[i].start.y < lines[i].end.y || (lines[i].start.y == lines[i].end.y && lines[i].start.x > lines[i].end.x)) {
                Point tmp = lines[i].start;
                lines[i].start = lines[i].end;
                lines[i].end = tmp;
            }
            i = i + 1;
        }
    }
                
    public void sortPointsByStart(Line[] lines, int start, int end) {
        if (end - start == 1) {
            return;
        }
        int mid = (start + end) / 2;
        sortPointsByStart(lines, start, mid);
        sortPointsByStart(lines, mid, end);
                
        int currentLength = end - start;
        Line[] tmpArray = new Line[currentLength];
        int i = start;
        int s = start;
        int t = mid;
        boolean shouldBreak = false;
        while(!shouldBreak && i < currentLength) {
            if (lines[s].start.y < lines[t].start.y || (lines[s].start.y == lines[t].start.y && lines[s].start.x < lines[t].start.x)) {
                tmpArray[i] = lines[s];
                s = s + 1;
            } else {
                tmpArray[i] = lines[t];
                t = t + 1;
            }
            i = i + 1;
            if (s == mid || t == end) {
                shouldBreak = true;
            }
        }
        while (s < mid) {
            tmpArray[i] = lines[s];
            i = i + 1;
            s = s + 1;
        }
        while (t < end) {
            tmpArray[i] = lines[t];
            i = i + 1;
            t = t + 1;
        }
        i = 0;
        while(i < currentLength) {
            lines[start + i] = tmpArray[i];
        }
    }
                
    public void sortPointsByEnd(Line[] lines, int start, int end) {
        if (end - start == 1) {
            return;
        }
        int mid = (start + end) / 2;
        sortPointsByStart(lines, start, mid);
        sortPointsByStart(lines, mid, end);
                
        int currentLength = end - start;
        Line[] tmpArray = new Line[currentLength];
        int i = start;
        int s = start;
        int t = mid;
        boolean shouldBreak = false;
        while(!shouldBreak && i < currentLength) {
            if (lines[s].end.y < lines[t].end.y || (lines[s].end.y == lines[t].end.y && lines[s].end.x < lines[t].end.x)) {
                tmpArray[i] = lines[s];
                s = s + 1;
            } else {
                tmpArray[i] = lines[t];
                t = t + 1;
            }
            i = i + 1;
            if (s == mid || t == end) {
                shouldBreak = true;
            }
        }
        while (s < mid) {
            tmpArray[i] = lines[s];
            i = i + 1;
            s = s + 1;
        }
        while (t < end) {
            tmpArray[i] = lines[t];
            i = i + 1;
            t = t + 1;
        }
        i = 0;
        while(i < currentLength) {
            lines[start + i] = tmpArray[i];
        }
    }
}
                
class SweeplineAlgorythmus {
}
                
class Generator {
                
    public int last;
    public int prime1;
    public int prime2;
    public int prime3;
    public int prime4;
                
    public void init() {
        last = 234;
        prime1 = 54643481;
        prime2 = 809357;
        prime3 = 12923359;
        prime4 = 783931;
    }
                
    public int randomInt() {
        last = last * prime1;
        last = last + prime2;
        last = last * prime3;
        last = last + prime4;
        return last;
    }
                
    public Point generatePoint() {
        Point p = new Point();
        p.x = randomInt() % 64;
        p.y = randomInt() % 64;
        return p;
    }
                
    public Line generate(int id) {
        Line line = new Line();
        line.id = id;
        line.start = generatePoint();
        line.end = generatePoint();
        return line;
    }
                
}
                
class Main {
                
    public static void main(String[] args) {
        int num_lines = 20;
        Line[] lines = new Line[num_lines];
        Line[] start = new Line[num_lines];
        Line[] end = new Line[num_lines];
                
        Generator gen = new Generator();
        gen.init();
                
        int i = 0;
        while(i < num_lines) {
            lines[i] = gen.generate(i);
            start[i] = lines[i];
            end[i] = lines[i];
        }
       
        LineSorter sorter = new LineSorter();
        sorter.sortPointsByStart(start, 0, num_lines);
        sorter.sortPointsByEnd(end, 0, num_lines);
                
        /*
         * todo implement more
         */
    }
}
                        """;

        Lexer lexer = Lexer.forString(sweepline);
        TokenOccurrence token = lexer.nextToken();
        StringBuilder sb = new StringBuilder();
        do {
            OutputMessageHandler.clearDebugEvents();
            append(sb, token);
            Lexer curLexer = Lexer.forString(sb.toString());
            Parser parser = new Parser(curLexer);
            Program prog = parser.parseProgram();
            List<DebugEvent> eventList = OutputMessageHandler.getEvents();
            if (eventList.isEmpty()) {
                System.out.println(curLexer.printSourceText(curLexer.peek().position()));
            }
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
        Parser parser = new Parser(Lexer.forString(sweepline));
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
}
