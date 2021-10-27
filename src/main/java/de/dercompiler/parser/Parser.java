package de.dercompiler.parser;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.LexerErrorIds;

import java.util.ArrayList;
import java.util.List;

import static de.dercompiler.lexer.token.Token.*;

public class Parser {

    Lexer lexer;
    PrecedenceParser precedenceParser;
    private final OutputMessageHandler logger;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.precedenceParser = new PrecedenceParser(lexer, this);
        this.logger = new OutputMessageHandler(MessageOrigin.PARSER, System.err);
    }
    
    public Program parseProgram() {
        List<ClassDeclaration> classes = new ArrayList<>();
        while (lexer.nextToken() == CLASS) {
            classes.add(parseClassDeclaration());
        }
        if (lexer.nextToken() != EOF) {
            logger.printErrorAndExit(ParserErrorIds.TODO, "Expected class declaration");
        }
        return new Program(classes);
    }
    
    public ClassDeclaration parseClassDeclaration() {
        
    }

    public AbstractExpression parseUnaryExp() {
        return null;
    }

}
